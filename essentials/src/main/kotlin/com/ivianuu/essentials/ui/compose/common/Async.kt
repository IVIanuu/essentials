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

import androidx.compose.Composable
import androidx.ui.core.Dp
import com.ivianuu.essentials.ui.compose.common.scrolling.ScrollableList
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.core.invokeAsComposable
import com.ivianuu.essentials.util.Async
import com.ivianuu.essentials.util.Fail
import com.ivianuu.essentials.util.Loading
import com.ivianuu.essentials.util.Success
import com.ivianuu.essentials.util.Uninitialized

@Composable
fun <T> AsyncList(
    state: Async<List<T>>,
    itemSize: Dp,
    fail: @Composable() (Throwable) -> Unit = {},
    loading: @Composable() () -> Unit = { FullScreenLoading() },
    uninitialized: @Composable() () -> Unit = loading,
    successItem: @Composable() (Int, T) -> Unit
) = composable {
    Async(
        state = state,
        fail = fail,
        loading = loading,
        uninitialized = uninitialized,
        success = { items ->
            ScrollableList(
                items = items,
                itemSize = itemSize
            ) { index, item ->
                successItem.invokeAsComposable(index, item)
            }
        }
    )
}

@Composable
fun <T> AsyncList(
    state: Async<List<T>>,
    fail: @Composable() (Throwable) -> Unit = {},
    loading: @Composable() () -> Unit = { FullScreenLoading() },
    uninitialized: @Composable() () -> Unit = loading,
    successItem: @Composable() (Int, T) -> Unit
) = composable {
    Async(
        state = state,
        fail = fail,
        loading = loading,
        uninitialized = uninitialized,
        success = { items ->
            ScrollableList {
                items.forEachIndexed { index, item ->
                    successItem.invokeAsComposable(index, item)
                }
            }
        }
    )
}

@Composable
fun <T> Async(
    state: Async<T>,
    fail: @Composable() (Throwable) -> Unit = {},
    loading: @Composable() () -> Unit = { FullScreenLoading() },
    uninitialized: @Composable() () -> Unit = loading,
    success: @Composable() (T) -> Unit
) = composable {
    when (state) {
        Uninitialized -> uninitialized.invokeAsComposable()
        is Loading -> loading.invokeAsComposable()
        is Success -> success.invokeAsComposable(state())
        is Fail -> fail.invokeAsComposable(state.error)
    }
}
