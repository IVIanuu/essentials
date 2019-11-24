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

import android.os.Parcelable
import androidx.compose.Composable
import androidx.compose.State
import androidx.compose.sourceLocation
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.ivianuu.essentials.ui.compose.core.ActivityAmbient
import com.ivianuu.essentials.ui.compose.core.ambient
import com.ivianuu.essentials.ui.compose.core.onDispose
import com.ivianuu.essentials.ui.compose.core.state
import com.ivianuu.essentials.ui.compose.injekt.inject
import com.ivianuu.essentials.ui.compose.viewmodel.viewModel

inline fun <T : Parcelable> parceled(
    keepAcrossCompositions: Boolean = false,
    noinline init: () -> T
) = parceled(
    key = Integer.valueOf(sourceLocation()).toString(),
    keepAcrossCompositions = keepAcrossCompositions,
    init = init
)

@Composable
fun <T : Parcelable> parceled(
    key: String,
    keepAcrossCompositions: Boolean = false,
    init: () -> T
): T = parceledState(key, keepAcrossCompositions, init).value

@Composable
inline fun <T : Parcelable> parceledState(
    keepAcrossCompositions: Boolean = false,
    noinline init: () -> T
) = parceledState(
    key = Integer.valueOf(sourceLocation()).toString(),
    keepAcrossCompositions = keepAcrossCompositions,
    init = init
)

@Composable
fun <T : Parcelable> parceledState(
    key: String,
    keepAcrossCompositions: Boolean = false,
    init: () -> T
): State<T> {
    val viewModel = viewModel<ParceledStateViewModel>(
        factory = SavedStateViewModelFactory(
            inject(),
            ambient(ActivityAmbient) as SavedStateRegistryOwner
        )
    )

    val state = state {
        if (viewModel.handle.contains(key)) {
            viewModel.handle.get(key)!!
        } else {
            init()
        }
    }

    if (!keepAcrossCompositions) {
        onDispose {
            viewModel.handle.remove<T>(key)
        }
    }

    viewModel.handle.set(key, state.value)

    return state
}

@PublishedApi
internal class ParceledStateViewModel(val handle: SavedStateHandle) : ViewModel()