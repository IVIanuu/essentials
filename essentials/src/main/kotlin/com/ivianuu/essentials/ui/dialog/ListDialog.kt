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

package com.ivianuu.essentials.ui.dialog

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.dp
import androidx.ui.foundation.Clickable
import androidx.ui.layout.Container
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.EdgeInsets
import androidx.ui.layout.LayoutExpandedWidth
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ripple.Ripple
import com.ivianuu.essentials.ui.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.layout.MainAxisAlignment
import com.ivianuu.essentials.ui.layout.Row
import com.ivianuu.essentials.ui.layout.WidthSpacer
import com.ivianuu.essentials.ui.material.EmphasisProvider

// todo

@Composable
fun SimpleDialogListItem(
    leading: (@Composable() () -> Unit)? = null,
    title: @Composable() () -> Unit,
    onClick: (() -> Unit)? = null
) {
    Ripple(bounded = true) {
        Clickable(onClick = onClick) {
            Container(
                modifier = LayoutExpandedWidth,
                constraints = DpConstraints(
                    minHeight = 48.dp
                ),
                padding = EdgeInsets(
                    left = 24.dp,
                    right = 24.dp
                ),
                alignment = Alignment.CenterLeft
            ) {
                EmphasisProvider(emphasis = MaterialTheme.emphasisLevels().high) {
                    Row(
                        mainAxisAlignment = MainAxisAlignment.End,
                        crossAxisAlignment = CrossAxisAlignment.Center
                    ) {
                        if (leading != null) {
                            leading()

                            WidthSpacer(24.dp)
                        }

                        title()
                    }
                }
            }
        }
    }
}
