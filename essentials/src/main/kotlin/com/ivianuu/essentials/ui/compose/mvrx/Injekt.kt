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

package com.ivianuu.essentials.ui.compose.mvrx

import androidx.compose.ambient
import androidx.compose.effectOf
import androidx.compose.memo
import androidx.compose.unaryPlus
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.ivianuu.essentials.ui.compose.injekt.ComponentAmbient
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.mvrx.MvRxViewModel
import com.ivianuu.essentials.util.defaultViewModelKey
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.ParametersDefinition
import com.ivianuu.injekt.Type
import com.ivianuu.injekt.typeOf
import kotlin.reflect.KClass

inline fun <reified T : MvRxViewModel<*>> mvRxViewModel(
    from: ViewModelStoreOwner = +inject<ViewModelStoreOwner>(),
    key: String = +memo { T::class.defaultViewModelKey },
    name: Any? = null,
    noinline parameters: ParametersDefinition? = null
) = mvRxViewModel(typeOf<T>(), from, key, name, parameters)

fun <T : MvRxViewModel<*>> mvRxViewModel(
    type: Type<T>,
    from: ViewModelStoreOwner = +inject<ViewModelStoreOwner>(),
    key: String = +memo { (type.raw as KClass<T>).defaultViewModelKey },
    name: Any? = null,
    parameters: ParametersDefinition? = null
) = effectOf<T> {
    val component = +ambient(ComponentAmbient)

    val factory = +memo<ViewModelProvider.Factory> {
        InjektMvRxViewModelFactory(component, type, name, parameters)
    }

    return@effectOf +mvRxViewModel(
        type = type.raw as KClass<T>,
        from = from,
        key = key,
        factory = factory
    )
}

private class InjektMvRxViewModelFactory<T : MvRxViewModel<*>>(
    private val component: Component,
    private val type: Type<T>,
    private val name: Any?,
    private val parameters: ParametersDefinition?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T =
        component.get(type, name, parameters) as T
}