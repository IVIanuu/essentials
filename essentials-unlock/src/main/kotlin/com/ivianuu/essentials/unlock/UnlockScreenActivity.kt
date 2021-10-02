/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.unlock

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.ivianuu.essentials.SystemBuildInfo
import com.ivianuu.essentials.broadcast.BroadcastsFactory
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.ActivityScope
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.android.activityScope
import com.ivianuu.injekt.scope.ScopeElement
import com.ivianuu.injekt.scope.requireElement
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.take

/**
 * Requests a screen unlock
 */
class UnlockScreenActivity : ComponentActivity() {
  private var hasResult = false
  private var isValid = true
  private lateinit var requestId: String

  @SuppressLint("NewApi")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (!intent.hasExtra(KEY_REQUEST_ID)) {
      isValid = false
      finish()
      return
    }

    requestId = intent.getStringExtra(KEY_REQUEST_ID)!!

    @Provide val component: UnlockScreenComponent = requireElement(activityScope)

    log(logger = component.logger) { "unlock screen for $requestId" }

    fun finishWithResult(success: Boolean) {
      log(logger = component.logger) { "finish with result $success" }
      hasResult = true
      onUnlockScreenResult(requestId, success)
      finish()
    }

    window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
    window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
    window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)

    if (component.systemBuildInfo.sdk >= 26) {
      component.keyguardManager.requestDismissKeyguard(
        this,
        object :
          KeyguardManager.KeyguardDismissCallback() {
          override fun onDismissSucceeded() {
            super.onDismissSucceeded()
            finishWithResult(true)
          }

          override fun onDismissCancelled() {
            super.onDismissCancelled()
            finishWithResult(false)
          }

          override fun onDismissError() {
            super.onDismissError()
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

  override fun onDestroy() {
    // just in case we didn't respond yet
    if (isValid && !hasResult) {
      onUnlockScreenResult(requestId, false)
    }
    super.onDestroy()
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

@Provide @ScopeElement<ActivityScope>
class UnlockScreenComponent(
  val broadcastsFactory: BroadcastsFactory,
  val keyguardManager: @SystemService KeyguardManager,
  val logger: Logger,
  val systemBuildInfo: SystemBuildInfo
)
