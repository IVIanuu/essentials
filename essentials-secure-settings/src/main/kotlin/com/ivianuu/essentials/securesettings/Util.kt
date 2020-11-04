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

package com.ivianuu.essentials.securesettings

import androidx.compose.foundation.AmbientContentColor
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.popTop
import com.ivianuu.essentials.util.showToastRes
import com.ivianuu.injekt.FunApi
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext

@Composable
internal fun SecureSettingsHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.body2.copy(color = AmbientContentColor.current.copy(alpha = 0.6f)),
        modifier = Modifier.padding(all = 16.dp)
    )
}

@FunBinding
suspend fun popNavigatorOnceSecureSettingsGranted(
    hasSecureSettingsPermission: hasSecureSettingsPermission,
    showToastRes: showToastRes,
    navigator: Navigator,
    @FunApi toast: Boolean = true
) {
    // we check the permission periodically to automatically pop this screen
    // once we got the permission
    while (coroutineContext.isActive) {
        if (hasSecureSettingsPermission()) {
            if (toast) showToastRes(R.string.es_secure_settings_permission_granted)
            navigator.popTop(result = true)
            break
        }
        delay(100)
    }
}
