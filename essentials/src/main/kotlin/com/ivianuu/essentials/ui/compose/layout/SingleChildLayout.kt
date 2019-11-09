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

package com.ivianuu.essentials.ui.compose.layout

import androidx.compose.Composable
import androidx.ui.core.Constraints
import androidx.ui.core.Layout
import androidx.ui.core.Measurable
import androidx.ui.core.MeasureScope
import androidx.ui.core.Modifier
import com.ivianuu.essentials.ui.compose.core.composable

@Composable
fun SingleChildLayout(
    child: @Composable() () -> Unit,
    modifier: Modifier = Modifier.None,
    measureBlock: SingleChildMeasureBlock
) = composable("SingleChildLayout") {
    Layout(children = child, modifier = modifier) { measureables, constraints ->
        check(measureables.size <= 1)
        measureBlock(measureables.firstOrNull(), constraints)
    }
}

typealias SingleChildMeasureBlock = MeasureScope.(Measurable?, Constraints) -> MeasureScope.LayoutResult