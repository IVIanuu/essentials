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
import com.ivianuu.essentials.android.ui.common.ScrollableScreen
import com.ivianuu.essentials.android.ui.common.navigateOnClick
import com.ivianuu.essentials.android.ui.core.Text
import com.ivianuu.essentials.android.ui.coroutines.CoroutineScopeAmbient
import com.ivianuu.essentials.android.ui.injekt.inject
import com.ivianuu.essentials.android.ui.material.ListItem
import com.ivianuu.essentials.android.ui.navigation.Route
import com.ivianuu.essentials.android.util.Toaster
import kotlinx.coroutines.launch

/**
 * Asks the user for the secure settings permission
 */
fun SecureSettingsRoute(showHideNavBarHint: Boolean = false) = Route {
    popNavigatorOnceSecureSettingsGranted(toast = true)

    ScrollableScreen(title = stringResource(R.string.es_title_secure_settings)) {
        SecureSettingsHeader(
            stringResource(
                if (showHideNavBarHint) {
                    R.string.es_pref_secure_settings_header_hide_nav_bar_summary
                } else {
                    R.string.es_pref_secure_settings_header_summary
                }
            )
        )

        ListItem(
            title = { Text(stringResource(R.string.es_pref_use_pc)) },
            subtitle = { Text(stringResource(R.string.es_pref_use_pc_summary)) },
            onClick = navigateOnClick { SecureSettingsInstructionsRoute }
        )

        val coroutineScope = CoroutineScopeAmbient.current
        val secureSettingsHelper = inject<SecureSettingsHelper>()
        val toaster = inject<Toaster>()
        ListItem(
            title = { Text(stringResource(R.string.es_pref_use_root)) },
            subtitle = { Text(stringResource(R.string.es_pref_use_root_summary)) },
            onClick = {
                coroutineScope.launch {
                    if (secureSettingsHelper.grantWriteSecureSettingsViaRoot()) {
                        toaster.toast(R.string.es_secure_settings_permission_granted)
                    } else {
                        toaster.toast(R.string.es_secure_settings_no_root)
                    }
                }
            }
        )
    }
}
