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

package com.ivianuu.essentials.accessibility

import com.ivianuu.injekt.BindingContext
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.ModuleBuilder
import com.ivianuu.injekt.Name

@Name
annotation class AccessibilityComponents {
    companion object
}

inline fun <reified T : AccessibilityComponent> ModuleBuilder.bindAccessibilityComponent(
    name: Any? = null
) {
    withBinding<T>(name) { bindAccessibilityComponent() }
}

fun <T : AccessibilityComponent> BindingContext<T>.bindAccessibilityComponent(): BindingContext<T> {
    intoSet<AccessibilityComponent>(setName = AccessibilityComponents)
    return this
}

internal val AccessibilityComponentsModule = Module {
    set<AccessibilityComponent>(setName = AccessibilityComponents)
}
