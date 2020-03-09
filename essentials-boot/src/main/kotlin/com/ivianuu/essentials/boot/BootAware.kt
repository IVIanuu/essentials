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

package com.ivianuu.essentials.boot

import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.QualifierMarker
import com.ivianuu.injekt.common.map
import com.ivianuu.injekt.common.set

/**
 * Marks a component as boot aware
 */
interface BootAware

@QualifierMarker
annotation class BootAwareComponents {
    companion object : Qualifier.Element
}

inline fun <reified T : BootAware> ComponentBuilder.bindBootAwareIntoMap(
    componentQualifier: Qualifier = Qualifier.None
) {
    map<String, BootAware>(BootAwareComponents) {
        put<T>(entryKey = T::class.java.name, entryValueQualifier = componentQualifier)
    }
}

fun ComponentBuilder.esBootBindings() {
    map<String, BootAware>(mapQualifier = BootAwareComponents)
}
