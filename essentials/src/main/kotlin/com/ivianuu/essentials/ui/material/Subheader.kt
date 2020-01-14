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
import androidx.ui.core.Alignment
import androidx.ui.core.CurrentTextStyleProvider
import androidx.ui.core.dp
import androidx.ui.foundation.ProvideContentColor
import androidx.ui.graphics.Color
import androidx.ui.layout.Container
import androidx.ui.layout.LayoutPadding
import androidx.ui.material.MaterialTheme
import androidx.ui.text.TextStyle
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.core.ambientOf
import com.ivianuu.essentials.ui.core.current

@Immutable
data class SubheaderStyle(
    val textStyle: TextStyle,
    val textColor: Color
)

@Composable
fun DefaultSubheaderStyle(
    textStyle: TextStyle = MaterialTheme.typography().body2,
    textColor: Color = MaterialTheme.colors().secondary
) = SubheaderStyle(
    textStyle = textStyle,
    textColor = textColor
)

val SubheaderStyleAmbient = ambientOf<SubheaderStyle?> { null }

@Composable
fun Subheader(
    text: String,
    style: SubheaderStyle = SubheaderStyleAmbient.current ?: DefaultSubheaderStyle()
) {
    Subheader(style = style) { Text(text) }
}

@Composable
fun Subheader(
    style: SubheaderStyle = SubheaderStyleAmbient.current ?: DefaultSubheaderStyle(),
    text: @Composable() () -> Unit
) {
    Container(
        height = 48.dp,
        expanded = true,
        modifier = LayoutPadding(left = 16.dp, right = 16.dp),
        alignment = Alignment.CenterLeft
    ) {
        ProvideContentColor(color = style.textColor) {
            CurrentTextStyleProvider(value = style.textStyle, children = text)
        }
    }
}
