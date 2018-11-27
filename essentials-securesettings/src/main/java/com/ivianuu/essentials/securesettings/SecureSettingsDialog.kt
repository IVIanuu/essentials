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
import com.ivianuu.essentials.ui.base.BaseDialogController
import com.ivianuu.essentials.ui.traveler.NavOptions
import com.ivianuu.essentials.ui.traveler.dialog
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.ui.traveler.key.ResultKey
import com.ivianuu.essentials.ui.traveler.key.bindKey
import com.ivianuu.essentials.ui.traveler.vertical
import com.ivianuu.essentials.util.RequestCodeGenerator
import com.ivianuu.essentials.util.ext.toast
import com.ivianuu.traveler.navigate
import com.ivianuu.traveler.result.goBackWithResult
import com.ivianuu.traveler.result.sendResult
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.launch
import javax.inject.Inject

@Parcelize
class SecureSettingsKey(
    override val resultCode: Int = RequestCodeGenerator.generate()
) : ControllerKey(SecureSettingsDialog::class, NavOptions().dialog()), ResultKey<Boolean>

/**
 * @author Manuel Wrage (IVIanuu)
 */
class SecureSettingsDialog : BaseDialogController() {

    @Inject lateinit var shell: Shell

    private val key by bindKey<SecureSettingsKey>()

    override fun onCreateDialog(savedViewState: Bundle?): Dialog {
        return MaterialDialog(activity)
            .title(R.string.dialog_title_secure_settings)
            .message(R.string.dialog_message_secure_settings)
            .positiveButton(R.string.action_use_root) {
                coroutineScope.launch {
                    try {
                        shell.run("pm grant ${activity.packageName} android.permission.WRITE_SECURE_SETTINGS")
                        handlePermissionResult(activity.canWriteSecureSettings())
                    } catch (e: Exception) {
                        toast(R.string.msg_secure_settings_no_root)
                    }
                }
            }
            .negativeButton(R.string.action_pc_instructions) {
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
            toast(R.string.msg_secure_settings_permission_granted)
            travelerRouter.goBackWithResult(key.resultCode, true)
        } else {
            toast(R.string.msg_secure_settings_permission_denied)
        }
    }
}