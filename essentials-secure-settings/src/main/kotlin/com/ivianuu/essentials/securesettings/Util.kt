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

import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext

@Composable internal fun SecureSettingsHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.body2.copy(color = LocalContentColor.current.copy(alpha = 0.6f)),
        modifier = Modifier.padding(all = 16.dp)
    )
}

@GivenFun suspend fun popNavigatorOnceSecureSettingsGranted(
    toast: Boolean = true,
    @Given dispatchNavigationAction: DispatchAction<NavigationAction>,
    @Given hasSecureSettingsPermission: hasSecureSettingsPermission,
    @Given showToastRes: showToastRes
) {
    // we check the permission periodically to automatically pop this screen
    // once we got the permission
    while (coroutineContext.isActive) {
        if (hasSecureSettingsPermission()) {
            if (toast) showToastRes(R.string.es_secure_settings_permission_granted)
            dispatchNavigationAction(NavigationAction.PopTop(true))
            break
        }
        delay(100)
    }
}
