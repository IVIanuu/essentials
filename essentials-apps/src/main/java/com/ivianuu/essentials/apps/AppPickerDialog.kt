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

package com.ivianuu.essentials.apps

import android.app.Dialog
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItems
import com.ivianuu.essentials.ui.base.EsDialogController
import com.ivianuu.essentials.ui.traveler.NavOptions
import com.ivianuu.essentials.ui.traveler.dialog
import com.ivianuu.essentials.ui.traveler.key.ControllerKey
import com.ivianuu.essentials.ui.traveler.key.getKey
import com.ivianuu.essentials.util.ext.goBackWithResult
import com.ivianuu.essentials.util.string
import com.ivianuu.injekt.inject
import com.ivianuu.traveler.goBack
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.launch

@Parcelize
data class AppPickerKey(
    val title: String? = null,
    val launchableOnly: Boolean = false,
    val resultCode: Int
) : ControllerKey(AppPickerDialog::class, NavOptions().dialog())

/**
 * App picker
 */
class AppPickerDialog : EsDialogController() {

    private val appStore by inject<AppStore>()

    override fun onCreateDialog(savedViewState: Bundle?): Dialog {
        val apps = mutableListOf<AppInfo>()

        val key = getKey<AppPickerKey>()

        val dialog = MaterialDialog(activity)
            .title(text = key.title ?: string(R.string.es_dialog_title_app_picker))
            .negativeButton(R.string.es_action_cancel) { travelerRouter.goBack() }
            .noAutoDismiss()

        coroutineScope.launch {
            val newApps = if (key.launchableOnly) {
                appStore.launchableApps()
            } else {
                appStore.installedApps()
            }
            apps.clear()
            apps.addAll(newApps)

            dialog.listItems(
                items = apps.map { it.appName },
                waitForPositiveButton = false
            ) { _, index, _ ->
                val app = apps[index]
                travelerRouter.goBackWithResult(key.resultCode, app)
            }
        }

        return dialog
    }
}