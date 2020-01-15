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

package com.ivianuu.essentials.ui.material

import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.ui.graphics.Color
import androidx.ui.material.MaterialTheme
import com.ivianuu.essentials.ui.core.ambientOf
import com.ivianuu.essentials.ui.core.current

@Immutable
data class ProgressIndicatorStyle(val color: Color)

val ProgressIndicatorStyleAmbient = ambientOf<ProgressIndicatorStyle?> { null }

@Composable
fun DefaultProgressIndicatorStyle(color: Color = MaterialTheme.colors().secondary) =
    ProgressIndicatorStyle(color = color)

@Composable
fun LinearProgressIndicator(
    progress: Float,
    style: ProgressIndicatorStyle = ProgressIndicatorStyleAmbient.current ?: DefaultProgressIndicatorStyle()
) {
    androidx.ui.material.LinearProgressIndicator(
        progress = progress,
        color = style.color
    )
}

@Composable
fun LinearProgressIndicator(
    style: ProgressIndicatorStyle = ProgressIndicatorStyleAmbient.current ?: DefaultProgressIndicatorStyle()
) {
    androidx.ui.material.LinearProgressIndicator(color = style.color)
}

@Composable
fun CircularProgressIndicator(
    progress: Float,
    style: ProgressIndicatorStyle = ProgressIndicatorStyleAmbient.current ?: DefaultProgressIndicatorStyle()
) {
    androidx.ui.material.CircularProgressIndicator(
        progress = progress,
        color = style.color
    )
}

@Composable
fun CircularProgressIndicator(
    style: ProgressIndicatorStyle = ProgressIndicatorStyleAmbient.current ?: DefaultProgressIndicatorStyle()
) {
    androidx.ui.material.CircularProgressIndicator(color = style.color)
}
