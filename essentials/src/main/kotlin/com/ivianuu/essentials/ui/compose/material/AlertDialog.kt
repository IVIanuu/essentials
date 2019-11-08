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
import androidx.compose.unaryPlus
import androidx.ui.core.Alignment
import androidx.ui.core.CurrentTextStyleProvider
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
import androidx.ui.material.Divider
import androidx.ui.material.TextButtonStyle
import androidx.ui.material.surface.Card
import androidx.ui.material.themeTextStyle
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.dialog.Dialog
import com.ivianuu.essentials.ui.compose.dialog.dismissDialog

// todo remove hardcoded values

@Composable
fun AlertDialog(
    dismissOnOutsideTouch: Boolean = true,
    dismissOnBackClick: Boolean = true,
    showDividers: Boolean = false,
    applyContentPadding: Boolean = true,
    title: (@Composable() () -> Unit)? = null,
    content: (@Composable() () -> Unit)? = null,
    buttons: (@Composable() () -> Unit)? = null
) = composable("AlertDialog") {
    Dialog(
        dismissOnOutsideTouch = dismissOnOutsideTouch,
        dismissOnBackClick = dismissOnBackClick
    ) {
        Wrap(alignment = Alignment.Center) {
            Padding(
                left = 32.dp,
                top = 72.dp,
                right = 32.dp,
                bottom = 72.dp
            ) {
                PressGestureDetector {
                    ConstrainedBox(
                        constraints = DpConstraints(
                            minWidth = 280.dp
                        )
                    ) {
                        Card(
                            shape = RoundedCornerShape(size = 8.dp),
                            elevation = 24.dp
                        ) {
                            Column {
                                if (title != null) {
                                    Container(
                                        modifier = ExpandedWidth wraps Inflexible,
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

                                    if (title != null && showDividers) DialogDivider()

                                    Container(
                                        modifier = ExpandedWidth,
                                        alignment = Alignment.TopLeft,
                                        padding = EdgeInsets(
                                            left = if (applyContentPadding) 24.dp else 0.dp,
                                            right = if (applyContentPadding) 24.dp else 0.dp
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
                                    if (content != null && showDividers) {
                                        DialogDivider()
                                    } else if (content != null || title != null) {
                                        HeightSpacer(28.dp)
                                    }

                                    Container(
                                        modifier = Inflexible,
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
    val dismissDialog = +dismissDialog()
    Button(
        text = text.toUpperCase(), // todo find a better way for uppercase
        onClick = onClick?.let { nonNullOnClick ->
            {
                nonNullOnClick()
                if (dismissDialogOnClick) dismissDialog()
            }
        },
        style = TextButtonStyle()
    )
}

@Composable
fun DialogCloseButton(text: String) = composable("DialogCloseButton") {
    DialogButton(text = text, dismissDialogOnClick = true, onClick = {})
}

@Composable
private fun DialogDivider() = composable("DialogDivider") {
    Divider(color = (+colorForCurrentBackground()).copy(alpha = 0.12f))
}