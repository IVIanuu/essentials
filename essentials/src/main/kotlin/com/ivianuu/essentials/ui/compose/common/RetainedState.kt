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

package com.ivianuu.essentials.ui.compose.common

import androidx.compose.State
import androidx.compose.effectOf
import androidx.compose.onDispose
import androidx.compose.state
import androidx.lifecycle.ViewModel
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.viewmodel.viewModel

fun <T> retained(
    key: Any,
    keepAcrossCompositions: Boolean = false,
    init: () -> T
) = effectOf<T> {
    (+retainedState(key, keepAcrossCompositions, init)).value
}

fun <T> retainedState(
    key: Any,
    keepAcrossCompositions: Boolean = false,
    init: () -> T
) = effectOf<State<T>> {
    val viewModel = +viewModel<RetainedStateViewModel>(
        key = "RetainedState:${key.hashCode()}"
    )

    val state = +state {
        if (viewModel.values.containsKey(key)) {
            viewModel.values[key] as T
        } else {
            init()
        }
    }

    if (!keepAcrossCompositions) {
        composable("clear value") {
            +onDispose {
                viewModel.values.remove(key)
            }
        }
    }

    viewModel.values[key] = state.value

    return@effectOf state
}

@PublishedApi
internal class RetainedStateViewModel : ViewModel() {
    val values = mutableMapOf<Any, Any?>()
}