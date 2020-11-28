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
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.Scoped
import com.ivianuu.injekt.merge.ApplicationComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal typealias MutableAccessibilityServiceHolder = MutableStateFlow<AccessibilityService?>

@Scoped(ApplicationComponent::class)
@Binding
fun mutableAccessibilityServiceHolder(): MutableAccessibilityServiceHolder =
    MutableStateFlow(null)

typealias AccessibilityServiceHolder = StateFlow<AccessibilityService?>
@Binding
inline val MutableAccessibilityServiceHolder.accessibilityServiceHolder: AccessibilityServiceHolder
        get() = this
