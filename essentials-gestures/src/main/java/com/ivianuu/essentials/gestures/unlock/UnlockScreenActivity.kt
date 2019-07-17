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
import com.ivianuu.essentials.messaging.BroadcastFactory
import com.ivianuu.essentials.ui.base.EsActivity
import com.ivianuu.essentials.util.AppSchedulers
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.injekt.inject
import com.ivianuu.scopes.android.onDestroy
import com.ivianuu.scopes.rx.disposeBy

/**
 * Requests a screen unlock
 */
class UnlockScreenActivity : EsActivity() {

    private val broadcastFactory by inject<BroadcastFactory>()
    private val keyguardManager by inject<KeyguardManager>()
    private val schedulers by inject<AppSchedulers>()
    private val screenUnlocker by inject<ScreenUnlocker>()
    private val systemBuildInfo by inject<SystemBuildInfo>()

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
                .observeOn(schedulers.main)
                .subscribe { finishWithResult(it.action == Intent.ACTION_USER_PRESENT) }
                .disposeBy(onDestroy)
        }
    }

    private fun finishWithResult(success: Boolean) {
        screenUnlocker.screenUnlockResult(success)
        finish()
    }

}