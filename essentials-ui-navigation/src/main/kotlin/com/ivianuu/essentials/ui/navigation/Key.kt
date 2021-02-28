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

import com.ivianuu.essentials.util.cast
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenSetElement
import kotlin.reflect.KClass

interface Key<T>

class KeyModule<K : Key<*>>(private val keyClass: KClass<K>) {

    @GivenSetElement
    fun keyUiIntoSet(@Given keyUiFactory: (@Given K) -> KeyUi<K>): Pair<KClass<Key<Any>>, KeyUiFactory<Key<Any>>> =
        (keyClass to keyUiFactory).cast()

    @GivenSetElement
    fun keyUiOptionFactoryIntoSet(
        @Given keyUiOptionsFactory: KeyUiOptionsFactory<K> = noOpKeyUiOptionFactory()
    ): Pair<KClass<Key<Any>>, KeyUiOptionsFactory<Key<Any>>> =
        (keyClass to keyUiOptionsFactory).cast()

    companion object {
        inline operator fun <reified K : Key<*>> invoke() = KeyModule(K::class)
    }
}
