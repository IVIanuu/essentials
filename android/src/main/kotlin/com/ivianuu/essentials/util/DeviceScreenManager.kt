package com.ivianuu.essentials.util

import android.app.*
import android.content.*
import android.hardware.*
import android.os.*
import android.view.*
import androidx.activity.*
import androidx.compose.runtime.*
import androidx.lifecycle.*
import app.cash.molecule.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.Flow
import java.util.*
import java.util.concurrent.*
import kotlin.collections.set
import kotlin.time.Duration.Companion.seconds

@Stable @Provide class DeviceScreenManager(
  private val appContext: AppContext,
  broadcastManager: BroadcastManager,
  private val keyguardManager: @SystemService KeyguardManager,
  private val logger: Logger,
  private val powerManager: @SystemService PowerManager,
  private val windowManager: @SystemService WindowManager
) {
  val screenState: Flow<ScreenState> = broadcastManager.broadcasts(
    Intent.ACTION_SCREEN_OFF,
    Intent.ACTION_SCREEN_ON,
    Intent.ACTION_USER_PRESENT
  )
    .onStart<Any?> { emit(Unit) }
    .map {
      if (powerManager.isInteractive) {
        if (keyguardManager.isDeviceLocked) ScreenState.LOCKED
        else ScreenState.UNLOCKED
      } else {
        ScreenState.OFF
      }
    }
    .distinctUntilChanged()

  val screenRotation: Flow<ScreenRotation> = moleculeFlow(RecompositionMode.Immediate) {
    val screenState by screenState.collectAsState(ScreenState.OFF)
    var displayRotation by remember { mutableStateOf(getCurrentDisplayRotation()) }

    if (screenState.isOn)
      DisposableEffect(true) {
        val listener = object : OrientationEventListener(appContext, SensorManager.SENSOR_DELAY_NORMAL) {
          override fun onOrientationChanged(orientation: Int) {
            displayRotation = getCurrentDisplayRotation()
          }
        }
        listener.enable()
        onDispose { listener.disable() }
      }

    displayRotation
  }

  private fun getCurrentDisplayRotation() = when (windowManager.defaultDisplay.rotation) {
    Surface.ROTATION_0 -> ScreenRotation.PORTRAIT_UP
    Surface.ROTATION_90 -> ScreenRotation.LANDSCAPE_LEFT
    Surface.ROTATION_180 -> ScreenRotation.PORTRAIT_DOWN
    Surface.ROTATION_270 -> ScreenRotation.LANDSCAPE_RIGHT
    else -> error("Unexpected rotation")
  }

  suspend fun turnScreenOn(): Boolean {
    logger.d { "on request is off ? ${!powerManager.isInteractive}" }
    if (powerManager.isInteractive) {
      logger.d { "already on" }
      return true
    }

    return startUnlockActivityForResult(REQUEST_TYPE_SCREEN_ON)
  }

  suspend fun unlockScreen(): Boolean {
    logger.d { "on request is locked ? ${keyguardManager.isKeyguardLocked}" }
    if (!keyguardManager.isKeyguardLocked) {
      logger.d { "already unlocked" }
      return true
    }

    return startUnlockActivityForResult(REQUEST_TYPE_UNLOCK)
  }

  private suspend fun startUnlockActivityForResult(requestType: Int): Boolean {
    val result = CompletableDeferred<Boolean>()
    val requestId = UUID.randomUUID().toString()
    requestsById[requestId] = result
    appContext.startActivity(
      Intent(appContext, UnlockActivity::class.java).apply {
        putExtra(KEY_REQUEST_ID, requestId)
        putExtra(KEY_REQUEST_TYPE, requestType)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      }
    )
    return result.await()
  }
}

enum class ScreenState(val isOn: Boolean) {
  OFF(false), LOCKED(true), UNLOCKED(true)
}

private const val KEY_REQUEST_ID = "request_id"
private const val KEY_REQUEST_TYPE = "request_type"
private const val REQUEST_TYPE_UNLOCK = 0
private const val REQUEST_TYPE_SCREEN_ON = 1
private val requestsById = ConcurrentHashMap<String, CompletableDeferred<Boolean>>()

@Provide @AndroidComponent class UnlockActivity(
  private val keyguardManager: @SystemService KeyguardManager,
  private val logger: Logger,
  private val powerManager: @SystemService PowerManager
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

    logger.d {
      when (requestType) {
        REQUEST_TYPE_UNLOCK -> "unlock screen for $requestId"
        REQUEST_TYPE_SCREEN_ON -> "turn screen on $requestId"
        else -> throw AssertionError()
      }
    }

    var hasResult = false

    fun finishWithResult(success: Boolean) {
      logger.d { "finish with result $success" }
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

          keyguardManager.requestDismissKeyguard(
            this@UnlockActivity,
            object :
              KeyguardManager.KeyguardDismissCallback() {
              override fun onDismissSucceeded() {
                super.onDismissSucceeded()
                logger.d { "dismiss succeeded" }
                finishWithResult(true)
              }

              override fun onDismissError() {
                super.onDismissError()
                logger.d { "dismiss error" }
                finishWithResult(true)
              }

              override fun onDismissCancelled() {
                super.onDismissCancelled()
                logger.d { "dismiss cancelled" }
                finishWithResult(false)
              }
            }
          )
        }
        REQUEST_TYPE_SCREEN_ON -> {
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
