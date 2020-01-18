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

import androidx.compose.Composable
import androidx.ui.foundation.contentColor
import androidx.ui.layout.LayoutPadding
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp
import com.ivianuu.essentials.composehelpers.current
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.coroutines.launchOnActive
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.ui.layout.WithModifier
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient
import com.ivianuu.essentials.util.Toaster
import kotlinx.coroutines.delay

@Composable
internal fun SecureSettingsHeader(text: String) {
    WithModifier(modifier = LayoutPadding(all = 16.dp)) {
        val textColor = contentColor().copy(alpha = 0.6f)

        Text(
            text = text,
            style = MaterialTheme.typography().body2.copy(color = textColor)
        )
    }
}

@Composable
internal fun popNavigatorOnceSecureSettingsGranted(toast: Boolean) {
    val navigator = NavigatorAmbient.current
    val secureSettingsHelper = inject<SecureSettingsHelper>()
    val toaster = inject<Toaster>()

    // we check the permission periodically to automatically pop this screen
    // once we got the permission
    launchOnActive {
        while (true) {
            if (secureSettingsHelper.canWriteSecureSettings()) {
                if (toast) toaster.toast(R.string.es_secure_settings_permission_granted)
                navigator.popTop(result = true)
                break
            }
            delay(500)
        }
    }
}
