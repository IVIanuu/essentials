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

package com.ivianuu.essentials.ui.popup

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Shape
import androidx.ui.layout.padding
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.material.Surface

@Composable
fun Popup(
    shape: Shape = RoundedCornerShape(size = 4.dp),
    elevation: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    Surface(
        elevation = elevation,
        shape = shape
    ) {
        Box(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp), children = content)
    }
}
