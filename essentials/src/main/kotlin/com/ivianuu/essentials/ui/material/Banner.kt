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
import androidx.ui.core.Alignment
import androidx.ui.core.CurrentTextStyleProvider
import androidx.ui.layout.Container
import androidx.ui.layout.EdgeInsets
import androidx.ui.layout.LayoutHeight
import androidx.ui.layout.LayoutWidth
import androidx.ui.layout.Spacer
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.material.TextButtonStyle
import androidx.ui.unit.dp
import com.ivianuu.essentials.ui.layout.Column
import com.ivianuu.essentials.ui.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.layout.Row
import com.ivianuu.essentials.ui.layout.SpacingRow
import com.ivianuu.essentials.ui.layout.WithModifier

@Composable
fun Banner(
    leading: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
    actions: @Composable () -> Unit
) {
    WithModifier(modifier = LayoutWidth.Fill) {
        Column {
            Spacer(LayoutHeight(24.dp))

            Row(crossAxisAlignment = CrossAxisAlignment.Center) {
                Spacer(LayoutWidth(16.dp))

                if (leading != null) {
                    Providers(IconStyleAmbient provides AvatarIconStyle(), children = leading)
                    Spacer(LayoutWidth(16.dp))
                }

                CurrentTextStyleProvider(value = MaterialTheme.typography().body2) {
                    ProvideEmphasis(emphasis = EmphasisAmbient.current.high, children = content)
                }

                Spacer(LayoutWidth(16.dp))
            }

            Spacer(LayoutHeight(20.dp))

            Container(
                modifier = LayoutWidth.Fill,
                alignment = Alignment.CenterRight,
                padding = EdgeInsets(left = 8.dp, right = 8.dp)
            ) {
                Providers(ButtonStyleAmbient provides TextButtonStyle()) {
                    SpacingRow(spacing = 8.dp, children = actions)
                }
            }

            Spacer(LayoutHeight(8.dp))
        }
    }
}
