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
import androidx.ui.savedinstancestate.rememberSavedInstanceState
import com.ivianuu.essentials.util.dispatchers
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import java.io.Closeable

/**
 * Base view model
 */
@Reader
abstract class ViewModel : Closeable {

    val scope = CoroutineScope(dispatchers.default)

    override fun close() {
        scope.cancel()
    }
}

@Composable
inline fun <T : ViewModel> viewModel(
    vararg inputs: Any?,
    key: Any = currentComposer.currentCompoundKeyHash.toString(),
    noinline init: () -> T
) = rememberSavedInstanceState(inputs = *inputs, key = key.toString(), init = init)

@Reader
@Composable
inline fun <T : ViewModel> viewModel(
    vararg inputs: Any?,
    key: Any = currentComposer.currentCompoundKeyHash.toString()
) = viewModel(*inputs, key = key) { given<T>() }
