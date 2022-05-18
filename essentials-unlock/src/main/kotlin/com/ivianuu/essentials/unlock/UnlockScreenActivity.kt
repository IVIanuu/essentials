/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.unlock

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.ivianuu.essentials.AppElementsOwner
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.common.Element
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Requests a screen unlock
 */
class UnlockScreenActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val requestId = intent.getStringExtra(KEY_REQUEST_ID) ?: run {
      finish()
      return
    }

    @Provide val component = application
      .let { it as AppElementsOwner }
      .appElements<UnlockScreenComponent>()

    log(logger = component.logger) { "unlock screen for $requestId" }

    var hasResult = false

    fun finishWithResult(success: Boolean) {
      log(logger = component.logger) { "finish with result $success" }
      hasResult = true
      onUnlockScreenResult(requestId, success)
      finish()
    }

    window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
    window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
    window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)

    lifecycleScope.launchWhenResumed {
      delay(1000)

      component.keyguardManager.requestDismissKeyguard(
        this@UnlockScreenActivity,
        object :
          KeyguardManager.KeyguardDismissCallback() {
          override fun onDismissSucceeded() {
            super.onDismissSucceeded()
            log(logger = component.logger) { "dismiss succeeded" }
            finishWithResult(true)
          }

          override fun onDismissCancelled() {
            super.onDismissCancelled()
            log(logger = component.logger) { "dismiss cancelled" }
            finishWithResult(false)
          }

          override fun onDismissError() {
            super.onDismissError()
            log(logger = component.logger) { "dismiss error" }
            finishWithResult(false)
          }
        }
      )
    }

    lifecycleScope.launch(start = CoroutineStart.UNDISPATCHED) {
      onCancel {
        // just in case we didn't respond yet
        if (!hasResult)
          onUnlockScreenResult(requestId, false)
      }
    }
  }

  internal companion object {
    private const val KEY_REQUEST_ID = "request_id"
    fun unlock(context: Context, requestId: String) {
      context.startActivity(
        Intent(context, UnlockScreenActivity::class.java).apply {
          putExtra(KEY_REQUEST_ID, requestId)
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
      )
    }
  }
}

@Provide @Element<AppScope>
data class UnlockScreenComponent(
  val keyguardManager: @SystemService KeyguardManager,
  val logger: Logger
)
