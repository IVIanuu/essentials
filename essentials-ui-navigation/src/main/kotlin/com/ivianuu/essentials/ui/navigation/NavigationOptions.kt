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

package com.ivianuu.essentials.ui.navigation

import com.ivianuu.essentials.ui.animatedstack.StackTransition
import com.ivianuu.injekt.Arg
import com.ivianuu.injekt.Effect
import com.ivianuu.injekt.ForEffect
import com.ivianuu.injekt.MapEntries
import kotlin.reflect.KClass

data class NavigationOptions(
    val opaque: Boolean = false,
    val enterTransition: StackTransition? = null,
    val exitTransition: StackTransition? = null,
) {
    constructor(
        opaque: Boolean = false,
        transition: StackTransition,
    ) : this(opaque, transition, transition)
}

typealias NavigationOptionsFactory<K> = (K) -> NavigationOptions

@Effect
annotation class NavigationOptionsFactoryBinding<K> {
    companion object {
        @Suppress("UNCHECKED_CAST")
        @MapEntries
        inline fun <@Arg("K") reified K, T : NavigationOptionsFactory<K>> bind(
            factory: @ForEffect T,
        ): NavigationOptionFactories = mapOf(
            K::class as KClass<out Key> to factory as (Key) -> NavigationOptions
        )
    }
}

typealias NavigationOptionFactories = Map<KClass<out Key>, NavigationOptionsFactory<Key>>
