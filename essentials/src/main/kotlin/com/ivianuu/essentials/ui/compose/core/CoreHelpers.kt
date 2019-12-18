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

package com.ivianuu.essentials.ui.compose.core

import androidx.compose.Composable
import androidx.compose.composer
import com.ivianuu.essentials.util.sourceLocation

@Composable
inline fun <V1> call(
    v1: V1,
    block: @Composable() () -> Unit
) {
    composer.call(
        key = sourceLocation(),
        invalid = { changed(v1) },
        block = { block() }
    )
}

@Composable
inline fun <V1, V2> call(
    v1: V1,
    v2: V2,
    block: @Composable() () -> Unit
) {
    composer.call(
        key = sourceLocation(),
        invalid = { changed(v1) or changed(v2) },
        block = { block() }
    )
}

@Composable
inline fun call(
    vararg inputs: Any?,
    block: @Composable() () -> Unit
) {
    composer.call(
        key = sourceLocation(),
        invalid = { inputs.map { changed(it) }.any() },
        block = { block() }
    )
}