/*
 * Copyright 2019 Manuel Wrage
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
import androidx.compose.Composable
import androidx.lifecycle.lifecycleScope
import com.ivianuu.essentials.broadcast.BroadcastFactory
import com.ivianuu.essentials.ui.activity.EsActivity
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.android.activityComponent
import com.ivianuu.injekt.composition.runReader
import com.ivianuu.injekt.get
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take

/**
 * Requests a screen unlock
 */
class UnlockScreenActivity : EsActivity() {

    private var hasResult = false
    private var valid = true
    private lateinit var requestId: String

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityComponent.runReader {
            if (!intent.hasExtra(KEY_REQUEST_ID)) {
                valid = false
                finish()
                return
            }

            requestId = intent.getStringExtra(KEY_REQUEST_ID)!!

            d("unlock screen for $requestId")

            fun finishWithResult(success: Boolean) {
                d("finish with result $success")
                hasResult = true
                get<UnlockScreen>().onUnlockScreenResult(requestId, success)
                finish()
            }

            if (get<SystemBuildInfo>().sdk >= 26) {
                get<KeyguardManager>().requestDismissKeyguard(this, object :
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
                })
            } else {
                window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
                BroadcastFactory.create(
                    Intent.ACTION_SCREEN_OFF,
                    Intent.ACTION_SCREEN_ON,
                    Intent.ACTION_USER_PRESENT
                )
                    .take(1)
                    .onEach {
                        finishWithResult(it.action == Intent.ACTION_USER_PRESENT)
                    }
                    .launchIn(lifecycleScope)
            }
        }
    }

    @Composable
    override fun content() {
    }

    override fun onDestroy() {
        activityComponent.runReader {
            // just in case we didn't respond yet
            if (valid && !hasResult) {
                get<UnlockScreen>().onUnlockScreenResult(requestId, false)
            }
        }
        super.onDestroy()
    }

    internal companion object {
        private const val KEY_REQUEST_ID = "request_id"
        fun unlock(context: Context, requestId: String) {
            context.startActivity(Intent(context, UnlockScreenActivity::class.java).apply {
                putExtra(KEY_REQUEST_ID, requestId)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        }
    }
}
