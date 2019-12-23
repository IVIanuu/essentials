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

import android.os.Parcelable
import androidx.compose.Composable
import androidx.compose.State
import androidx.compose.ambient
import androidx.compose.onDispose
import androidx.compose.state
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModel
import com.ivianuu.essentials.ui.core.ActivityAmbient
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.ui.viewmodel.viewModel
import com.ivianuu.essentials.util.sourceLocation

// todo mirror the retained objects api

@Composable
inline fun <T : Parcelable> parceledState(
    noinline init: () -> T
): State<T> = parceledState(
    key = sourceLocation().toString(),
    init = init
)

@Composable
fun <T : Parcelable> parceledState(
    key: String,
    init: () -> T
): State<T> {
    val factory = SavedStateViewModelFactory(
        inject(),
        ambient(ActivityAmbient)
    )
    val viewModel = viewModel(
        factory = { factory.create(ParceledStateViewModel::class.java) }
    )

    val state = state {
        if (viewModel.handle.contains(key)) {
            viewModel.handle.get(key)!!
        } else {
            init()
        }
    }

    onDispose { viewModel.handle.remove<T>(key) }

    viewModel.handle.set(key, state.value)

    return state
}

@PublishedApi
internal class ParceledStateViewModel(val handle: SavedStateHandle) : ViewModel()
