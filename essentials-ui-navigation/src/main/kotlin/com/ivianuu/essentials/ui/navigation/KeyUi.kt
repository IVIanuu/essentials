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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import com.ivianuu.injekt.Given
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.StateFlow

typealias KeyUi<K> = @Composable () -> Unit

typealias KeyUiFactory<K> = (K, KeyUiGivenScope) -> KeyUi<K>

typealias ViewModelKeyUi<K, VM, S> = @Composable (VM, S) -> Unit

class ViewModelKeyUiModule<T : ViewModelKeyUi<K, VM, S>, K : Key<*>, VM : StateFlow<S>, S>(keyClass: KClass<K>) {
    @Given
    val keyModule = KeyModule(keyClass)

    @Given
    fun keyUi(
        @Given uiFactory: () ->T,
        @Given viewModel: VM
    ): KeyUi<K> = {
        val ui = remember(uiFactory) as @Composable (VM, S) -> Unit
        ui(viewModel, viewModel.collectAsState().value)
    }

    companion object {
        @Given
        inline operator fun <@Given T : ViewModelKeyUi<K, VM, S>, reified K : Key<*>,
                VM : StateFlow<S>, S> invoke() = ViewModelKeyUiModule<T, K, VM, S>(K::class)
    }
}
