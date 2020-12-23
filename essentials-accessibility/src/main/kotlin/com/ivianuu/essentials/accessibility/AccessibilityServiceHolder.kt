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
import com.ivianuu.essentials.sourcekey.memo
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.component.ApplicationScoped
import com.ivianuu.injekt.component.Storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal typealias MutableAccessibilityServiceHolder = MutableStateFlow<AccessibilityService?>

@Given fun mutableAccessibilityServiceHolder(
    @Given storage: Storage<ApplicationScoped>
): MutableAccessibilityServiceHolder = storage.memo {
    MutableStateFlow(null)
}

typealias AccessibilityServiceHolder = StateFlow<AccessibilityService?>
@Given inline val @Given MutableAccessibilityServiceHolder.accessibilityServiceHolder: AccessibilityServiceHolder
        get() = this
