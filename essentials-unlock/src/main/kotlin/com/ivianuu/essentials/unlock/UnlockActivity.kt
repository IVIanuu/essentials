/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.unlock

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PowerManager
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.ivianuu.essentials.AppElementsOwner
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.time.seconds
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.common.Element
import com.ivianuu.injekt.inject
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.yield
import java.util.concurrent.ConcurrentHashMap

/**
 * Requests a screen unlock
 */
class UnlockActivity : ComponentActivity() {
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

    @Provide val component = application
      .cast<AppElementsOwner>()
      .appElements
      .element<UnlockComponent>()

    log {
      when (requestType) {
        REQUEST_TYPE_UNLOCK -> "unlock screen for $requestId"
        REQUEST_TYPE_SCREEN_ON -> "turn screen on $requestId"
        else -> throw AssertionError()
      }
    }

    var hasResult = false

    fun finishWithResult(success: Boolean) {
      log { "finish with result $success" }
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
          delay(50)

          component.keyguardManager().requestDismissKeyguard(
            this@UnlockActivity,
            object :
              KeyguardManager.KeyguardDismissCallback() {
              override fun onDismissSucceeded() {
                super.onDismissSucceeded()
                log { "dismiss succeeded" }
                finishWithResult(true)
              }

              override fun onDismissError() {
                super.onDismissError()
                log { "dismiss error" }
                finishWithResult(true)
              }

              override fun onDismissCancelled() {
                super.onDismissCancelled()
                log { "dismiss cancelled" }
                finishWithResult(false)
              }
            }
          )
        }
      }
      REQUEST_TYPE_SCREEN_ON -> {
        lifecycleScope.launch {
          val powerManager = component.powerManager()

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

  internal companion object {
    private const val KEY_REQUEST_ID = "request_id"
    private const val KEY_REQUEST_TYPE = "request_type"
    private const val REQUEST_TYPE_UNLOCK = 0
    private const val REQUEST_TYPE_SCREEN_ON = 1

    context(Context) fun unlockScreen(requestId: String) {
      startActivity(
        Intent(inject(), UnlockActivity::class.java).apply {
          putExtra(KEY_REQUEST_ID, requestId)
          putExtra(KEY_REQUEST_TYPE, REQUEST_TYPE_UNLOCK)
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
      )
    }

    context(Context) fun turnScreenOn(requestId: String) {
      startActivity(
        Intent(inject(), UnlockActivity::class.java).apply {
          putExtra(KEY_REQUEST_ID, requestId)
          putExtra(KEY_REQUEST_TYPE, REQUEST_TYPE_SCREEN_ON)
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
      )
    }
  }
}

@Provide @Element<AppScope>
data class UnlockComponent(
  val keyguardManager: () -> KeyguardManager,
  @Provide val logger: Logger,
  val powerManager: () -> PowerManager
)

internal val requestsById = ConcurrentHashMap<String, CompletableDeferred<Boolean>>()
