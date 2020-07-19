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

package com.ivianuu.essentials.gestures

import android.view.accessibility.AccessibilityEvent
import com.ivianuu.essentials.accessibility.AccessibilityConfig
import com.ivianuu.essentials.accessibility.AccessibilityServices
import com.ivianuu.essentials.util.GlobalScope
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.d
import com.ivianuu.essentials.util.globalScope
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

// todo make this class a single shared flow

@Given(ApplicationComponent::class)
class SecureScreenDetector {

    private val _isOnSecureScreen = MutableStateFlow(false)
    val isOnSecureScreen: StateFlow<Boolean> get() = _isOnSecureScreen

    init {
        val services = given<AccessibilityServices>()
        services.applyConfig(
            AccessibilityConfig(
                eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            )
        )

        services.events
            .onEach { onAccessibilityEvent(it) }
            .launchIn(globalScope)
    }

    private fun onAccessibilityEvent(event: AccessibilityEvent) {
        // ignore keyboards
        if (event.className == "android.inputmethodservice.SoftInputWindow") {
            return
        }

        val packageName = event.packageName?.toString() ?: return
        val className = event.className?.toString() ?: return

        // val managePermissionsActivity = "com.android.packageinstaller.permission.ui.ManagePermissionsActivity"
        // val grantPermissionsActivity ="com.android.packageinstaller.permission.ui.GrantPermissionsActivity"

        var isOnSecureScreen = "packageinstaller" in packageName

        if (!isOnSecureScreen) {
            isOnSecureScreen = packageName == "com.android.settings" &&
                    className == "android.app.MaterialDialog"
        }

        // distinct
        d { "on secure screen changed: $isOnSecureScreen" }
        _isOnSecureScreen.value = isOnSecureScreen
    }
}
