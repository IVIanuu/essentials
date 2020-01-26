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

package com.ivianuu.essentials.securesettings

import androidx.ui.res.stringResource
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.filled.Link
import com.ivianuu.essentials.ui.common.ScrollableScreen
import com.ivianuu.essentials.ui.common.openUrlOnClick
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.painter.VectorRenderable
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.essentials.util.Toaster

/**
 * Asks the user for the secure settings permission
 */
val SecureSettingsInstructionsRoute = Route {
    popNavigatorOnceSecureSettingsGranted(toast = false)

    ScrollableScreen(title = stringResource(R.string.es_title_secure_settings_pc_instructions)) {
        SecureSettingsHeader(
            text = stringResource(R.string.es_pref_secure_settings_pc_instructions_header_summary)
        )

        ListItem(
            title = stringResource(R.string.es_pref_secure_settings_step_1),
            subtitle = stringResource(R.string.es_pref_secure_settings_step_1_summary)
        )

        ListItem(
            title = stringResource(R.string.es_pref_secure_settings_step_2),
            subtitle = stringResource(R.string.es_pref_secure_settings_step_2_summary)
        )

        ListItem(
            title = stringResource(R.string.es_pref_secure_settings_step_3),
            subtitle = stringResource(R.string.es_pref_secure_settings_step_3_summary)
        )

        ListItem(
            image = VectorRenderable(Icons.Default.Link),
            title = stringResource(R.string.es_pref_secure_settings_link_gadget_hacks_summary),
            onClick = openUrlOnClick { "https://youtu.be/CDuxcrrWLnY" }
        )

        ListItem(
            image = VectorRenderable(Icons.Default.Link),
            title = stringResource(R.string.es_pref_secure_settings_link_lifehacker_summary),
            onClick = openUrlOnClick {
                "https://lifehacker.com/the-easiest-way-to-install-androids-adb-and-fastboot-to-1586992378"
            }
        )

        ListItem(
            image = VectorRenderable(Icons.Default.Link),
            title = stringResource(R.string.es_pref_secure_settings_link_xda_summary),
            onClick = openUrlOnClick {
                "https://www.xda-developers.com/install-adb-windows-macos-linux/"
            }
        )

        val buildInfo = inject<BuildInfo>()
        val clipboardAccessor = inject<ClipboardAccessor>()
        val toaster = inject<Toaster>()

        ListItem(
            title = stringResource(R.string.es_pref_secure_settings_step_4),
            subtitle = stringResource(
                R.string.es_pref_secure_settings_step_4_summary,
                buildInfo.packageName
            ),
            onClick = {
                clipboardAccessor.clipboardText =
                    "adb shell pm grant ${buildInfo.packageName} android.permission.WRITE_SECURE_SETTINGS"

                toaster.toast(R.string.es_copied_to_clipboard)
            }
        )
    }
}
