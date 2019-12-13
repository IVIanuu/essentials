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

package com.ivianuu.essentials.ui.compose.material

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.CurrentTextStyleProvider
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.layout.Container
import androidx.ui.layout.Padding
import androidx.ui.material.MaterialTheme

@Composable
fun Subheader(text: String) {
    Subheader { Text(text) }
}

@Composable
fun Subheader(text: @Composable() () -> Unit) {
    Container(
        height = 48.dp,
        expanded = true,
        alignment = Alignment.CenterLeft
    ) {
        Padding(left = 16.dp, right = 16.dp) {
            val textStyle = MaterialTheme.typography().body2.copy(
                color = MaterialTheme.colors().secondary
            )
            CurrentTextStyleProvider(value = textStyle, children = text)
        }
    }
}
