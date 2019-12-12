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

import com.ivianuu.injekt.BindingContext
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.ModuleBuilder
import com.ivianuu.injekt.Name

/**
 * Marks a component as boot aware
 */
interface BootAware

@Name
annotation class BootAwareComponents {
    companion object
}

inline fun <reified T : BootAware> ModuleBuilder.bindBootAware(
    name: Any? = null
) {
    withBinding<T>(name) { bindBootAware() }
}

inline fun <reified T : BootAware> BindingContext<T>.bindBootAware(): BindingContext<T> {
    intoMap<String, BootAware>(
        entryKey = T::class.java.name, mapName = BootAwareComponents
    )
    return this
}

val EsBootModule = Module {
    map<String, BootAware>(mapName = BootAwareComponents)
}
