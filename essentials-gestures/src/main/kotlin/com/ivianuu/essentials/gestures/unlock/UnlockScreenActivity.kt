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

package com.ivianuu.essentials.gestures.unlock

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import com.ivianuu.essentials.broadcast.BroadcastFactory
import com.ivianuu.essentials.ui.base.EsActivity
import com.ivianuu.essentials.util.AppDispatchers
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.injekt.inject
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.withContext

/**
 * Requests a screen unlock
 */
class UnlockScreenActivity : EsActivity() {

    private val broadcastFactory: BroadcastFactory by inject()
    private val dispatchers: AppDispatchers by inject()
    private val keyguardManager: KeyguardManager by inject()
    private val screenUnlocker: ScreenUnlocker by inject()
    private val systemBuildInfo: SystemBuildInfo by inject()

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (systemBuildInfo.sdk >= 26) {
            keyguardManager.requestDismissKeyguard(this, object :
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
            broadcastFactory.create(
                Intent.ACTION_SCREEN_OFF,
                Intent.ACTION_SCREEN_ON,
                Intent.ACTION_USER_PRESENT
            )
                .take(1)
                .onEach {
                    withContext(dispatchers.main) {
                        finishWithResult(it.action == Intent.ACTION_USER_PRESENT)
                    }
                }
                .launchIn(lifecycleScope)
        }
    }

    override fun content() {
    }

    private fun finishWithResult(success: Boolean) {
        screenUnlocker.screenUnlockResult(success)
        finish()
    }
}
