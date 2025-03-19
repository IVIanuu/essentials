package essentials.util

import android.app.*
import android.content.*
import android.os.*
import android.view.*
import androidx.activity.*
import androidx.compose.runtime.*
import androidx.core.content.*
import androidx.lifecycle.*
import essentials.app.*
import essentials.coroutines.*
import essentials.logging.*
import injekt.*
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.*
import kotlin.time.Duration.Companion.seconds

enum class ScreenState(val isOn: Boolean) { OFF(false), LOCKED(true), UNLOCKED(true) }

@Provide @Composable
fun screenState(context: Application = inject): ScreenState = broadcastStateOf(
  Intent.ACTION_SCREEN_OFF,
  Intent.ACTION_SCREEN_ON,
  Intent.ACTION_USER_PRESENT
) {
  if (context.getSystemService<PowerManager>()!!.isInteractive) {
    if (context.getSystemService<KeyguardManager>()!!.isDeviceLocked) ScreenState.LOCKED
    else ScreenState.UNLOCKED
  } else {
    ScreenState.OFF
  }
}

enum class ScreenRotation(val isPortrait: Boolean) {
  // 0 degrees
  PORTRAIT_UP(true),

  // 90 degrees
  LANDSCAPE_LEFT(false),

  // 180 degrees
  PORTRAIT_DOWN(true),

  // 270 degrees
  LANDSCAPE_RIGHT(false)
}

@Provide @Composable fun screenRotation(
  context: Application,
  screenState: ScreenState
): ScreenRotation {
  fun getCurrentDisplayRotation() = when (context.getSystemService<WindowManager>()!!.defaultDisplay.rotation) {
    Surface.ROTATION_0 -> ScreenRotation.PORTRAIT_UP
    Surface.ROTATION_90 -> ScreenRotation.LANDSCAPE_LEFT
    Surface.ROTATION_180 -> ScreenRotation.PORTRAIT_DOWN
    Surface.ROTATION_270 -> ScreenRotation.LANDSCAPE_RIGHT
    else -> error("Unexpected rotation")
  }

  var displayRotation by remember {
    mutableStateOf(getCurrentDisplayRotation())
  }

  if (screenState.isOn)
    DisposableEffect(true) {
      val listener = object : OrientationEventListener(
        context,
        android.hardware.SensorManager.SENSOR_DELAY_NORMAL
      ) {
        override fun onOrientationChanged(orientation: Int) {
          displayRotation = getCurrentDisplayRotation()
        }
      }
      listener.enable()
      onDispose { listener.disable() }
    }

  return displayRotation
}

@Tag typealias turnScreenOnResult = Boolean
typealias turnScreenOn = suspend () -> turnScreenOnResult

@Provide suspend fun turnScreenOn(
  context: Application,
  logger: Logger = inject
): turnScreenOnResult {
  val powerManager = context.getSystemService<PowerManager>()!!
  d { "on request is off ? ${!powerManager.isInteractive}" }
  if (powerManager.isInteractive) {
    d { "already on" }
    return true
  }

  return startUnlockActivityForResult(context, REQUEST_TYPE_SCREEN_ON)
}

@Tag typealias unlockScreenResult = Boolean
typealias unlockScreen = suspend () -> unlockScreenResult

@Provide suspend fun unlockScreen(
  context: Application,
  logger: Logger = inject
): unlockScreenResult {
  val keyguardManager = context.getSystemService<KeyguardManager>()!!
  d { "on request is locked ? ${keyguardManager.isKeyguardLocked}" }
  if (!keyguardManager.isKeyguardLocked) {
    d { "already unlocked" }
    return true
  }

  return startUnlockActivityForResult(context, REQUEST_TYPE_UNLOCK)
}

private suspend fun startUnlockActivityForResult(context: Application, requestType: Int): Boolean {
  val result = CompletableDeferred<Boolean>()
  val requestId = UUID.randomUUID().toString()
  requestsById[requestId] = result
  context.startActivity(
    Intent(context, UnlockActivity::class.java).apply {
      putExtra(KEY_REQUEST_ID, requestId)
      putExtra(KEY_REQUEST_TYPE, requestType)
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
  )
  return result.await()
}

private const val KEY_REQUEST_ID = "request_id"
private const val KEY_REQUEST_TYPE = "request_type"
private const val REQUEST_TYPE_UNLOCK = 0
private const val REQUEST_TYPE_SCREEN_ON = 1
private val requestsById = ConcurrentHashMap<String, CompletableDeferred<Boolean>>()

@Provide @AndroidComponent class UnlockActivity(
  @property:Provide private val logger: Logger
) : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val requestId = intent.getStringExtra(KEY_REQUEST_ID) ?: run {
      finish()
      return
    }

    val requestType = intent.getIntExtra(KEY_REQUEST_TYPE, -1)

    if (requestType != REQUEST_TYPE_UNLOCK && requestType != REQUEST_TYPE_SCREEN_ON) {
      finish()
      return
    }

    d {
      when (requestType) {
        REQUEST_TYPE_UNLOCK -> "unlock screen for $requestId"
        REQUEST_TYPE_SCREEN_ON -> "turn screen on $requestId"
        else -> throw AssertionError()
      }
    }

    var hasResult = false

    fun finishWithResult(success: Boolean) {
      d { "finish with result $success" }
      hasResult = true
      requestsById.remove(requestId)?.complete(success)
      finish()
    }

    window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
    window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)

    lifecycleScope.launch(start = CoroutineStart.UNDISPATCHED) {
      onCancel {
        // just in case we didn't respond yet
        if (!hasResult)
          requestsById.remove(requestId)?.complete(false)
      }
    }

    lifecycleScope.launch {
      when (requestType) {
        REQUEST_TYPE_UNLOCK -> {
          delay(250)

          getSystemService<KeyguardManager>()!!.requestDismissKeyguard(
            this@UnlockActivity,
            object :
              KeyguardManager.KeyguardDismissCallback() {
              override fun onDismissSucceeded() {
                super.onDismissSucceeded()
                d { "dismiss succeeded" }
                finishWithResult(true)
              }

              override fun onDismissError() {
                super.onDismissError()
                d { "dismiss error" }
                finishWithResult(true)
              }

              override fun onDismissCancelled() {
                super.onDismissCancelled()
                d { "dismiss cancelled" }
                finishWithResult(false)
              }
            }
          )
        }
        REQUEST_TYPE_SCREEN_ON -> {
          val powerManager = getSystemService<PowerManager>()!!
          withTimeoutOrNull(1.seconds) {
            while (!powerManager.isInteractive)
              yield()
          } ?: run {
            finishWithResult(false)
            return@launch
          }
          finishWithResult(true)
        }
      }
    }
  }
}
