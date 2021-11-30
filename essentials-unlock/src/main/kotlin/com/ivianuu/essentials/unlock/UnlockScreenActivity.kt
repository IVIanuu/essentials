/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.unlock

import android.annotation.*
import android.app.*
import android.content.*
import android.os.*
import android.view.*
import androidx.activity.*
import androidx.lifecycle.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.common.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Requests a screen unlock
 */
class UnlockScreenActivity : ComponentActivity() {
  @SuppressLint("NewApi")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val requestId = intent.getStringExtra(KEY_REQUEST_ID) ?: run {
      finish()
      return
    }

    @Provide val component = application
      .cast<AppElementsOwner>()
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

      if (component.systemBuildInfo.sdk >= 26) {
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
      } else {
        component.broadcastsFactory(
          Intent.ACTION_SCREEN_OFF,
          Intent.ACTION_SCREEN_ON,
          Intent.ACTION_USER_PRESENT
        )
          .map { it.action == Intent.ACTION_USER_PRESENT }
          .onStart {
            if (!component.keyguardManager.isKeyguardLocked)
              emit(true)
          }
          .take(1)
          .onEach { finishWithResult(it) }
          .launchIn(lifecycleScope)
      }
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
  val broadcastsFactory: BroadcastsFactory,
  val keyguardManager: @SystemService KeyguardManager,
  val logger: Logger,
  val systemBuildInfo: SystemBuildInfo
)
