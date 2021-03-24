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

package com.ivianuu.essentials.systemoverlay

import com.ivianuu.essentials.accessibility.AccessibilityConfig
import com.ivianuu.essentials.accessibility.AccessibilityEvent
import com.ivianuu.essentials.accessibility.AndroidAccessibilityEvent
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.ScopeCoroutineScope
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

typealias IsOnSecureScreen = Boolean

@Given
fun isOnSecureScreen(
    @Given accessibilityEvents: Flow<AccessibilityEvent>,
    @Given logger: Logger,
    @Given scope: ScopeCoroutineScope<AppGivenScope>,
): @Scoped<AppGivenScope> Flow<IsOnSecureScreen> = accessibilityEvents
    .filter { it.type == AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED }
    .map { it.packageName to it.className }
    .filter { it.second != "android.inputmethodservice.SoftInputWindow" }
    .map { (packageName, className) ->
        var isOnSecureScreen = "packageinstaller" in packageName.orEmpty()
        if (!isOnSecureScreen) {
            isOnSecureScreen = packageName == "com.android.settings" &&
                    className == "android.app.MaterialDialog"
        }

        isOnSecureScreen
    }
    .distinctUntilChanged()
    .onEach { logger.d { "on secure screen changed: $it" } }
    .stateIn(scope, SharingStarted.WhileSubscribed(1000), false)

@Given
val isOnSecureScreenAccessibilityConfig: Flow<AccessibilityConfig> = flow {
    emit(
        AccessibilityConfig(
            eventTypes = AndroidAccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        )
    )
}