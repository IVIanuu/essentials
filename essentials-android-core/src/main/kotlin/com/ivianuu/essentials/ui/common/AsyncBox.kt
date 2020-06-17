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

package com.ivianuu.essentials.ui.common

import androidx.compose.Composable
import androidx.compose.StructurallyEqual
import androidx.compose.mutableStateOf
import androidx.compose.remember
import androidx.ui.core.Modifier
import androidx.ui.material.CircularProgressIndicator
import com.ivianuu.essentials.ui.animatedstack.AnimatedBox
import com.ivianuu.essentials.ui.animatedstack.StackTransition
import com.ivianuu.essentials.ui.animatedstack.animation.FadeStackTransition
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.util.Async
import com.ivianuu.essentials.util.Fail
import com.ivianuu.essentials.util.Loading
import com.ivianuu.essentials.util.Success
import com.ivianuu.essentials.util.Uninitialized

@Composable
fun <T> AsyncLazyColumn(
    state: Async<List<T>>,
    modifier: Modifier = Modifier,
    transition: StackTransition = FadeStackTransition(),
    fail: @Composable (Throwable) -> Unit = { throw it },
    loading: @Composable () -> Unit = {
        CircularProgressIndicator(modifier = Modifier.center())
    },
    uninitialized: @Composable () -> Unit = loading,
    successEmpty: @Composable () -> Unit = {},
    successItem: @Composable (T) -> Unit
) {
    AsyncBox(
        state = state,
        modifier = modifier,
        transition = transition,
        fail = fail,
        loading = loading,
        uninitialized = uninitialized,
        success = { items ->
            if (items.isNotEmpty()) {
                AdapterList(data = items, item = successItem)
            } else {
                successEmpty()
            }
        }
    )
}

@Composable
fun <T> AsyncBox(
    state: Async<T>,
    modifier: Modifier = Modifier,
    transition: StackTransition = FadeStackTransition(),
    fail: @Composable (Throwable) -> Unit = {},
    loading: @Composable () -> Unit = {
        CircularProgressIndicator(
            modifier = Modifier.center()
        )
    },
    uninitialized: @Composable () -> Unit = loading,
    success: @Composable (T) -> Unit
) {
    val asyncState = remember(state::class) { mutableStateOf(state, StructurallyEqual) }
    asyncState.value = state

    AnimatedBox(
        current = asyncState,
        modifier = modifier,
        transition = transition
    ) { currentState ->
        when (val currentAsyncState = currentState.value) {
            is Uninitialized -> uninitialized()
            is Loading -> loading()
            is Success -> success(currentAsyncState.value)
            is Fail -> fail(currentAsyncState.error)
        }
    }
}
