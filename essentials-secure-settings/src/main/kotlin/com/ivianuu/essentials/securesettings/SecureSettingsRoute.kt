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
import com.ivianuu.essentials.composehelpers.current
import com.ivianuu.essentials.ui.common.ListScreen
import com.ivianuu.essentials.ui.common.navigateOnClick
import com.ivianuu.essentials.ui.coroutines.CoroutineScopeAmbient
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.util.Toaster
import kotlinx.coroutines.launch

/**
 * Asks the user for the secure settings permission
 */
fun SecureSettingsRoute(showHideNavBarHint: Boolean = false) = Route {
    popNavigatorOnceSecureSettingsGranted(toast = true)

    ListScreen(title = stringResource(R.string.es_title_secure_settings)) {
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
            title = stringResource(R.string.es_pref_use_pc),
            subtitle = stringResource(R.string.es_pref_use_pc_summary),
            onClick = navigateOnClick { SecureSettingsInstructionsRoute }
        )

        val coroutineScope = CoroutineScopeAmbient.current
        val secureSettingsHelper = inject<SecureSettingsHelper>()
        val toaster = inject<Toaster>()
        ListItem(
            title = stringResource(R.string.es_pref_use_root),
            subtitle = stringResource(R.string.es_pref_use_root_summary),
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
