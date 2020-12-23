/*
 * Copyright 2020 Manuel Wrage
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
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.ivianuu.essentials.broadcast.broadcasts
import com.ivianuu.essentials.componentElementBinding
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenGroup
import com.ivianuu.injekt.GivenSetElement
import com.ivianuu.injekt.android.ActivityScoped
import com.ivianuu.injekt.android.activityComponent
import com.ivianuu.injekt.component.Component
import com.ivianuu.injekt.component.get
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take

/**
 * Requests a screen unlock
 */
class UnlockScreenActivity : AppCompatActivity() {

    private var hasResult = false
    private var valid = true
    private lateinit var requestId: String

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!intent.hasExtra(KEY_REQUEST_ID)) {
            valid = false
            finish()
            return
        }

        requestId = intent.getStringExtra(KEY_REQUEST_ID)!!

        val dependencies = activityComponent[UnlockScreenDependencies]

        dependencies.logger.d { "unlock screen for $requestId" }

        fun finishWithResult(success: Boolean) {
            dependencies.logger.d { "finish with result $success" }
            hasResult = true
            onUnlockScreenResult(requestId, success)
            finish()
        }

        if (dependencies.systemBuildInfo.sdk >= 26) {
            dependencies.keyguardManager.requestDismissKeyguard(
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
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
            merge(
                dependencies.broadcasts(Intent.ACTION_SCREEN_OFF),
                dependencies.broadcasts(Intent.ACTION_SCREEN_ON),
                dependencies.broadcasts(Intent.ACTION_USER_PRESENT)
            )
                .take(1)
                .onEach {
                    finishWithResult(it.action == Intent.ACTION_USER_PRESENT)
                }
                .launchIn(lifecycleScope)
        }
    }

    override fun onDestroy() {
        // just in case we didn't respond yet
        if (valid && !hasResult) {
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

@Given class UnlockScreenDependencies(
    @Given val broadcasts: broadcasts,
    @Given val keyguardManager: KeyguardManager,
    @Given val logger: Logger,
    @Given val systemBuildInfo: SystemBuildInfo
) {
    companion object : Component.Key<UnlockScreenDependencies> {
        @GivenGroup val binding =
            componentElementBinding(ActivityScoped, UnlockScreenDependencies)
    }
}
