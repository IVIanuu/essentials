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

typealias NavigationOptionFactoryBinding = Pair<KClass<*>, (Key) -> NavigationOptions>

@Suppress("UNCHECKED_CAST")
inline fun <reified K : Key, T : (K) -> NavigationOptions> navigationOptionFactoryBinding():
        @GivenSetElement (@Given (K) -> NavigationOptions) -> NavigationOptionFactoryBinding = {
    K::class to it as (Key) -> NavigationOptions
}
