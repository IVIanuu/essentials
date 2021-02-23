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
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenSetElement
import com.ivianuu.injekt.Macro
import com.ivianuu.injekt.Qualifier
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

typealias NavigationOptionFactory = Pair<KClass<*>, (Key) -> NavigationOptions>

@Qualifier
annotation class NavigationOptionFactoryBinding
@Suppress("UNCHECKED_CAST")

@Macro
@GivenSetElement
inline fun <T : @NavigationOptionFactoryBinding (K) -> NavigationOptions, reified K : Key> navigationOptionFactoryBindingImpl(
    @Given instance: T
): NavigationOptionFactory = (K::class to instance) as NavigationOptionFactory
