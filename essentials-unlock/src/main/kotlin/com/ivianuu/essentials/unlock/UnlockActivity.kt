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
import com.ivianuu.essentials.AndroidComponent
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.seconds
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
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

  internal companion object {
    private const val KEY_REQUEST_ID = "request_id"
    private const val KEY_REQUEST_TYPE = "request_type"
    private const val REQUEST_TYPE_UNLOCK = 0
    private const val REQUEST_TYPE_SCREEN_ON = 1

    fun unlockScreen(context: Context, requestId: String) {
      context.startActivity(
        Intent(context, UnlockActivity::class.java).apply {
          putExtra(KEY_REQUEST_ID, requestId)
          putExtra(KEY_REQUEST_TYPE, REQUEST_TYPE_UNLOCK)
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
      )
    }

    fun turnScreenOn(context: Context, requestId: String) {
      context.startActivity(
        Intent(context, UnlockActivity::class.java).apply {
          putExtra(KEY_REQUEST_ID, requestId)
          putExtra(KEY_REQUEST_TYPE, REQUEST_TYPE_SCREEN_ON)
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
      )
    }
  }
}

internal val requestsById = ConcurrentHashMap<String, CompletableDeferred<Boolean>>()
