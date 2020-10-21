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

package com.ivianuu.essentials.ui

import androidx.compose.runtime.Composable
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.SetElements
import com.ivianuu.injekt.android.ActivityComponent
import com.ivianuu.injekt.merge.BindingModule

@BindingModule(ActivityComponent::class)
annotation class UiDecoratorBinding {
    @Module
    class ModuleImpl<T : @Composable (@Composable () -> Unit) -> Unit> {
        @SetElements
        operator fun <T : @Composable (@Composable () -> Unit) -> Unit> invoke(instance: T): UiDecorators = setOf(instance)
    }
}

typealias UiDecorators = Set<@Composable (@Composable () -> Unit) -> Unit>
@SetElements
fun defaultUiDecorators(): UiDecorators = emptySet()

@FunBinding
@Composable
fun DecorateUi(decorators: UiDecorators, children: @Assisted @Composable () -> Unit) {
    decorators.fold(children) { acc, decorator -> { decorator(acc) } }()
}