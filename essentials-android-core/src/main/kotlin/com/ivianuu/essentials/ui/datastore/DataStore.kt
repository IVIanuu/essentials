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

package com.ivianuu.essentials.ui.datastore

import androidx.compose.Composable
import androidx.compose.MutableState
import androidx.compose.collectAsState
import androidx.compose.key
import androidx.compose.remember
import com.ivianuu.essentials.store.DataStore
import com.ivianuu.essentials.ui.coroutines.compositionScope
import kotlinx.coroutines.launch

@Composable
fun <T> DataStore<T>.asState(): MutableState<T> = key(this) {
    val scope = compositionScope()
    val state = data.collectAsState(defaultData) as MutableState<T>
    remember {
        ObservableState(state) { newData ->
            scope.launch {
                updateData { newData }
            }
        }
    }
}

private class ObservableState<T>(
    val delegate: MutableState<T>,
    val onWrite: (T) -> Unit
) : MutableState<T> by delegate {
    override var value: T
        get() = delegate.value
        set(value) {
            delegate.value = value
            onWrite(value)
        }
}
