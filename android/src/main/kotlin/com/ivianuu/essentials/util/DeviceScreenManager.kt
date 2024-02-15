package com.ivianuu.essentials.util

import android.app.KeyguardManager
import android.content.Intent
import android.os.Bundle
import android.os.PowerManager
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.lifecycle.lifecycleScope
import com.ivianuu.essentials.AndroidComponent
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.SystemService
import com.ivianuu.essentials.compose.compositionFlow
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.yield
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.seconds

interface DeviceScreenManager {
  val screenState: Flow<ScreenState>

  suspend fun turnScreenOn(): Boolean

  suspend fun unlockScreen(): Boolean
}

enum class ScreenState(val isOn: Boolean) {
  OFF(false), LOCKED(true), UNLOCKED(true)
}

@Provide class DeviceScreenManagerImpl(
  private val appContext: AppContext,
  private val broadcastsFactory: BroadcastsFactory,
  private val keyguardManager: @SystemService KeyguardManager,
  private val logger: Logger,
  private val powerManager: @SystemService PowerManager
) : DeviceScreenManager {
  override val screenState: Flow<ScreenState> = compositionFlow {
    remember {
      broadcastsFactory(
        Intent.ACTION_SCREEN_OFF,
        Intent.ACTION_SCREEN_ON,
        Intent.ACTION_USER_PRESENT
      ).map { it.action }
    }.collectAsState(null).value

    if (powerManager.isInteractive) {
      if (keyguardManager.isDeviceLocked) ScreenState.LOCKED
      else ScreenState.UNLOCKED
    } else {
      ScreenState.OFF
    }
  }

  override suspend fun turnScreenOn(): Boolean {
    logger.log { "on request is off ? ${!powerManager.isInteractive}" }
    if (powerManager.isInteractive) {
      logger.log { "already on" }
      return true
    }

    return startUnlockActivityForResult(REQUEST_TYPE_SCREEN_ON)
  }

  override suspend fun unlockScreen(): Boolean {
    logger.log { "on request is locked ? ${keyguardManager.isKeyguardLocked}" }
    if (!keyguardManager.isKeyguardLocked) {
      logger.log { "already unlocked" }
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

    logger.log {
      when (requestType) {
        REQUEST_TYPE_UNLOCK -> "unlock screen for $requestId"
        REQUEST_TYPE_SCREEN_ON -> "turn screen on $requestId"
        else -> throw AssertionError()
      }
    }

    var hasResult = false

    fun finishWithResult(success: Boolean) {
      logger.log { "finish with result $success" }
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

    when (requestType) {
      REQUEST_TYPE_UNLOCK -> {
        lifecycleScope.launch {
          delay(250)

          keyguardManager.requestDismissKeyguard(
            this@UnlockActivity,
            object :
              KeyguardManager.KeyguardDismissCallback() {
              override fun onDismissSucceeded() {
                super.onDismissSucceeded()
                logger.log { "dismiss succeeded" }
                finishWithResult(true)
              }

              override fun onDismissError() {
                super.onDismissError()
                logger.log { "dismiss error" }
                finishWithResult(true)
              }

              override fun onDismissCancelled() {
                super.onDismissCancelled()
                logger.log { "dismiss cancelled" }
                finishWithResult(false)
              }
            }
          )
        }
      }
      REQUEST_TYPE_SCREEN_ON -> {
        lifecycleScope.launch {
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
