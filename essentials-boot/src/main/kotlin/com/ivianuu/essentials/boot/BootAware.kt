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

import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.composition.BindingEffect
import com.ivianuu.injekt.composition.BindingEffectFunction
import com.ivianuu.injekt.map
import kotlin.reflect.KClass

/**
 * Marks a component as boot aware
 */
interface BootAware

@BindingEffect(ApplicationComponent::class)
annotation class BindBootAware

@BindingEffectFunction(BindBootAware::class)
@Module
inline fun <reified T : BootAware> bindBootAwareIntoMap() {
    map<KClass<out BootAware>, BootAware> {
        put<T>(T::class)
    }
}

@Module
fun esBootModule() {
    map<KClass<out BootAware>, BootAware>()
}
