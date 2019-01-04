/*
 * Copyright 2018 Manuel Wrage
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

package com.ivianuu.essentials.securesettings

import android.app.Dialog
import android.os.Bundle
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.ivianuu.essentials.shell.Shell
import com.ivianuu.essentials.ui.base.EsDialogController
import com.ivianuu.essentials.ui.traveler.NavOptions
import com.ivianuu.essentials.ui.traveler.dialog
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.ui.traveler.key.key
import com.ivianuu.essentials.ui.traveler.vertical
import com.ivianuu.essentials.util.ext.goBackWithResult
import com.ivianuu.essentials.util.ext.sendResult
import com.ivianuu.essentials.util.ext.toast
import com.ivianuu.injekt.inject
import com.ivianuu.traveler.navigate
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.launch

@Parcelize
class SecureSettingsKey(
    val resultCode: Int
) : ControllerKey(::SecureSettingsDialog, NavOptions().dialog())

/**
 * @author Manuel Wrage (IVIanuu)
 */
class SecureSettingsDialog : EsDialogController() {

    private val shell by inject<Shell>()

    private val key by key<SecureSettingsKey>()

    override fun onCreateDialog(savedViewState: Bundle?): Dialog {
        return MaterialDialog(activity)
            .title(R.string.es_dialog_title_secure_settings)
            .message(R.string.es_dialog_message_secure_settings)
            .positiveButton(R.string.es_action_use_root) {
                coroutineScope.launch {
                    try {
                        shell.run("pm grant ${activity.packageName} android.permission.WRITE_SECURE_SETTINGS")
                        handlePermissionResult(activity.canWriteSecureSettings())
                    } catch (e: Exception) {
                        e.printStackTrace()
                        toast(R.string.es_msg_secure_settings_no_root)
                    }
                }
            }
            .negativeButton(R.string.es_action_pc_instructions) {
                travelerRouter.navigate(SecureSettingsInstructionsKey(), NavOptions().vertical())
            }
            .noAutoDismiss()
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        if (activity.canWriteSecureSettings()) {
            handlePermissionResult(true)
        }
    }

    override fun handleBack(): Boolean {
        travelerRouter.sendResult(key.resultCode, false)
        return super.handleBack()
    }

    private fun handlePermissionResult(success: Boolean) {
        if (success) {
            toast(R.string.es_msg_secure_settings_permission_granted)
            travelerRouter.goBackWithResult(key.resultCode, true)
        } else {
            toast(R.string.es_msg_secure_settings_permission_denied)
        }
    }
}