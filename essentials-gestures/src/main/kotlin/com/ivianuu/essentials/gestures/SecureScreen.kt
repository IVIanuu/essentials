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

package com.ivianuu.essentials.gestures

import com.ivianuu.essentials.accessibility.AccessibilityConfig
import com.ivianuu.essentials.accessibility.AccessibilityServices
import com.ivianuu.essentials.accessibility.AndroidAccessibilityEvent
import com.ivianuu.essentials.util.GlobalScope
import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.merge.ApplicationComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.shareIn

typealias IsOnSecureScreen = Flow<Boolean>

@Binding(ApplicationComponent::class)
fun isOnSecureScreen(
    globalScope: GlobalScope,
    logger: Logger,
    services: AccessibilityServices,
): IsOnSecureScreen {
    services.applyConfig(
        AccessibilityConfig(
            eventTypes = AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        )
    )
    return services.events
        .filter { it.type == AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED }
        .map { it.packageName to it.className }
        .filter { it.first != null && it.second != null }
        .map { it.first!! to it.second!! }
        .filter { it.second != "android.inputmethodservice.SoftInputWindow" }
        .map { (packageName, className) ->
            // val managePermissionsActivity = "com.android.packageinstaller.permission.ui.ManagePermissionsActivity"
            // val grantPermissionsActivity ="com.android.packageinstaller.permission.ui.GrantPermissionsActivity"

            var isOnSecureScreen = "packageinstaller" in packageName

            if (!isOnSecureScreen) {
                isOnSecureScreen = packageName == "com.android.settings" &&
                    className == "android.app.MaterialDialog"
            }

            isOnSecureScreen
        }
        .onStart { emit(false) }
        .distinctUntilChanged()
        .onEach { logger.d("on secure screen changed: $it") }
        .shareIn(globalScope, SharingStarted.WhileSubscribed(1000), 1)
}
