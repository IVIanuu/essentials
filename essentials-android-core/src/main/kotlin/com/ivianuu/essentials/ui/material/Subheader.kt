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
import androidx.compose.staticAmbientOf
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.ProvideContentColor
import androidx.ui.foundation.ProvideTextStyle
import androidx.ui.graphics.Color
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.LayoutPadding
import androidx.ui.layout.LayoutWidth
import androidx.ui.material.MaterialTheme
import androidx.ui.text.TextStyle
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.core.currentOrElse

@Immutable
data class SubheaderStyle(
    val modifier: Modifier,
    val textStyle: TextStyle,
    val textColor: Color
)

@Composable
fun DefaultSubheaderStyle(
    modifier: Modifier = Modifier.None,
    textStyle: TextStyle = MaterialTheme.typography().body2,
    textColor: Color = MaterialTheme.colors().secondary
) = SubheaderStyle(
    modifier = modifier,
    textStyle = textStyle,
    textColor = textColor
)

val SubheaderStyleAmbient = staticAmbientOf<SubheaderStyle>()

@Composable
fun Subheader(
    modifier: Modifier = Modifier.None,
    style: SubheaderStyle = SubheaderStyleAmbient.currentOrElse { DefaultSubheaderStyle() },
    text: @Composable () -> Unit
) {
    Box(
        modifier = LayoutHeight(48.dp) + LayoutWidth.Fill + LayoutPadding(
            start = 16.dp,
            end = 16.dp
        ) + style.modifier + modifier,
        gravity = ContentGravity.CenterStart
    ) {
        ProvideContentColor(color = style.textColor) {
            ProvideTextStyle(value = style.textStyle, children = text)
        }
    }
}
