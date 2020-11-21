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
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import com.ivianuu.essentials.activity.EsActivity
import com.ivianuu.essentials.broadcast.broadcasts
import com.ivianuu.essentials.coroutines.collectIn
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.injekt.android.ActivityComponent
import com.ivianuu.injekt.android.activityComponent
import com.ivianuu.injekt.merge.MergeInto
import com.ivianuu.injekt.merge.mergeComponent
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.take

/**
 * Requests a screen unlock
 */
class UnlockScreenActivity : EsActivity() {

    private var hasResult = false
    private var valid = true
    private lateinit var requestId: String

    private val component by lazy {
        activityComponent.mergeComponent<UnlockScreenActivityComponent>()
    }

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!intent.hasExtra(KEY_REQUEST_ID)) {
            valid = false
            finish()
            return
        }

        requestId = intent.getStringExtra(KEY_REQUEST_ID)!!

        component.logger.d("unlock screen for $requestId")

        fun finishWithResult(success: Boolean) {
            component.logger.d("finish with result $success")
            hasResult = true
            onUnlockScreenResult(requestId, success)
            finish()
        }

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
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
            merge(
                component.broadcasts(Intent.ACTION_SCREEN_OFF),
                component.broadcasts(Intent.ACTION_SCREEN_ON),
                component.broadcasts(Intent.ACTION_USER_PRESENT)
            )
                .take(1)
                .collectIn(lifecycleScope) {
                    finishWithResult(it.action == Intent.ACTION_USER_PRESENT)
                }
        }
    }

    @Composable
    override fun Content() {
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

@MergeInto(ActivityComponent::class)
interface UnlockScreenActivityComponent {
    val broadcasts: broadcasts
    val keyguardManager: KeyguardManager
    val logger: Logger
    val systemBuildInfo: SystemBuildInfo
}
