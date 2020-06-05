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

package com.ivianuu.essentials.ui.viewmodel

import androidx.compose.Composable
import androidx.compose.currentComposer
import com.ivianuu.essentials.ui.base.ViewModel
import com.ivianuu.essentials.ui.core.retain
import com.ivianuu.essentials.ui.injekt.compositionComponent
import com.ivianuu.injekt.composition.get

@Composable
fun <T : ViewModel> viewModel(
    key: Any = currentComposer.currentCompoundKeyHash,
    factory: () -> T
): T = retain(key = key, init = factory)

@Composable
fun <T : ViewModel> injectViewModel(
    key: Any = currentComposer.currentCompoundKeyHash,
): T {
    val component = compositionComponent
    return viewModel(key) { component.get() }
}
