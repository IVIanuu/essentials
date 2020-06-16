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
import androidx.compose.Providers
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentColorAmbient
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.ProvideTextStyle
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.material.MaterialTheme
import androidx.ui.unit.dp

@Composable
fun Subheader(
    modifier: Modifier = Modifier,
    text: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.preferredHeight(48.dp)
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
            .plus(modifier),
        gravity = ContentGravity.CenterStart
    ) {
        Providers(ContentColorAmbient provides MaterialTheme.colors.secondary) {
            ProvideTextStyle(value = MaterialTheme.typography.body2, children = text)
        }
    }
}
