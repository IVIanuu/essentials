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
import androidx.compose.ambient
import androidx.compose.unaryPlus
import androidx.ui.core.Alignment
import androidx.ui.core.CurrentTextStyleProvider
import androidx.ui.core.PxPosition
import androidx.ui.core.dp
import androidx.ui.core.gesture.PressGestureDetector
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.Column
import androidx.ui.layout.ConstrainedBox
import androidx.ui.layout.Container
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.EdgeInsets
import androidx.ui.layout.ExpandedWidth
import androidx.ui.layout.HeightSpacer
import androidx.ui.layout.MainAxisAlignment
import androidx.ui.layout.Padding
import androidx.ui.layout.Row
import androidx.ui.layout.Wrap
import androidx.ui.material.Button
import androidx.ui.material.TextButtonStyle
import androidx.ui.material.surface.Card
import androidx.ui.material.themeTextStyle
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.dialog.DismissDialogAmbient

// todo remove hardcoded values

fun AlertDialog(
    dismissOnOutsideTouch: Boolean = true,
    title: (@Composable() () -> Unit)? = null,
    content: (@Composable() () -> Unit)? = null,
    buttons: (@Composable() () -> Unit)? = null
) {
    val dismissDialog = +ambient(DismissDialogAmbient)

    PressGestureDetector(
        onPress = if (dismissOnOutsideTouch) {
            { _: PxPosition -> dismissDialog() }
        } else null
    ) {
        Wrap(alignment = Alignment.Center) {
            Padding(padding = DialogPadding) {
                PressGestureDetector {
                    ConstrainedBox(
                        constraints = DpConstraints(
                            minWidth = 280.dp
                        )
                    ) {
                        Card(
                            shape = RoundedCornerShape(size = DialogCornerRadius),
                            elevation = 24.dp
                        ) {
                            Column {
                                if (title != null) {
                                    Container(
                                        modifier = ExpandedWidth,
                                        alignment = Alignment.CenterLeft,
                                        padding = EdgeInsets(
                                            top = 24.dp,
                                            left = 24.dp,
                                            right = 24.dp
                                        )
                                    ) {
                                        CurrentTextStyleProvider(
                                            +themeTextStyle { h6 }
                                        ) {
                                            title()
                                        }
                                    }

                                    if (content == null && buttons == null) {
                                        HeightSpacer(24.dp)
                                    }
                                }

                                if (content != null) {
                                    HeightSpacer(24.dp)

                                    Container(
                                        modifier = ExpandedWidth,
                                        alignment = Alignment.TopLeft,
                                        padding = EdgeInsets(
                                            left = 24.dp,
                                            right = 24.dp
                                        )
                                    ) {
                                        CurrentTextStyleProvider(
                                            (+themeTextStyle { subtitle1 }).copy(
                                                color = (+colorForCurrentBackground()).copy(alpha = SecondaryTextAlpha)
                                            )
                                        ) {
                                            content()
                                        }
                                    }

                                    if (buttons == null) {
                                        HeightSpacer(24.dp)
                                    }
                                }

                                if (buttons != null) {
                                    if (content != null || title != null) {
                                        HeightSpacer(28.dp)
                                    }

                                    Container(
                                        expanded = true,
                                        alignment = Alignment.CenterRight,
                                        constraints = DpConstraints(
                                            minHeight = 52.dp
                                        )
                                    ) {
                                        Padding(padding = 8.dp) {
                                            Row(
                                                mainAxisAlignment = MainAxisAlignment.End,
                                                crossAxisAlignment = CrossAxisAlignment.Center
                                            ) {
                                                buttons()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DialogButton(
    text: String,
    dismissDialogOnClick: Boolean = true,
    onClick: (() -> Unit)? = null
) = composable("DialogButton") {
    val dismissDialog = +ambient(DismissDialogAmbient)
    Button(
        text = text,
        onClick = onClick?.let { nonNullOnClick ->
            {
                nonNullOnClick()
                if (dismissDialogOnClick) dismissDialog()
            }
        },
        style = TextButtonStyle()
    )
}

private val DialogCornerRadius = 8.dp
private val DialogPadding = 32.dp
private val DialogMinWidth = 280.dp