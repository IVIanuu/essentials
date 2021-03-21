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

package com.ivianuu.essentials.accessibility

import android.accessibilityservice.AccessibilityService
import android.app.Service
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.coroutines.runOnCancellation
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.AppGivenScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal typealias MutableAccessibilityServiceHolder = MutableStateFlow<AccessibilityService?>

@Given
fun mutableAccessibilityServiceHolder(): @Scoped<AppGivenScope> MutableAccessibilityServiceHolder =
    MutableStateFlow(null)

typealias AccessibilityServiceHolder = StateFlow<AccessibilityService?>

@Given
inline val @Given MutableAccessibilityServiceHolder.accessibilityServiceHolder: AccessibilityServiceHolder
        get() = this

@Given
fun accessibilityServiceHolderWorker(
    @Given holder: MutableAccessibilityServiceHolder,
    @Given service: Service
): ScopeWorker<AccessibilityGivenScope> = {
    holder.value = service as AccessibilityService
    runOnCancellation { holder.value = null }
}
