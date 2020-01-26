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
import androidx.ui.core.CurrentTextStyleProvider
import androidx.ui.core.Layout
import androidx.ui.core.Measurable
import androidx.ui.core.ParentData
import androidx.ui.core.Placeable
import androidx.ui.foundation.contentColor
import androidx.ui.layout.Container
import androidx.ui.layout.EdgeInsets
import androidx.ui.layout.LayoutWidth
import androidx.ui.layout.Spacer
import androidx.ui.material.Divider
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.unit.IntPx
import androidx.ui.unit.dp
import androidx.ui.unit.ipx
import com.ivianuu.essentials.ui.layout.Column
import com.ivianuu.essentials.ui.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.layout.MainAxisAlignment
import com.ivianuu.essentials.ui.layout.Row

// todo rename to dialog

@Composable
fun MaterialDialog(
    showTopDivider: Boolean = false,
    showBottomDivider: Boolean = false,
    applyContentPadding: Boolean = true,
    buttonLayout: AlertDialogButtonLayout = AlertDialogButtonLayout.SideBySide,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    content: @Composable (() -> Unit)? = null,
    positiveButton: @Composable (() -> Unit)? = null,
    negativeButton: @Composable (() -> Unit)? = null,
    neutralButton: @Composable (() -> Unit)? = null
) {
    Dialog {
        DialogBody(
            showTopDivider = showTopDivider,
            showBottomDivider = showBottomDivider,
            applyContentPadding = applyContentPadding,
            buttonLayout = buttonLayout,
            icon = icon,
            title = title,
            content = content,
            positiveButton = positiveButton,
            negativeButton = negativeButton,
            neutralButton = neutralButton
        )
    }
}

enum class AlertDialogButtonLayout {
    SideBySide,
    Stacked
}

@Composable
private fun DialogBody(
    showTopDivider: Boolean = false,
    showBottomDivider: Boolean = false,
    applyContentPadding: Boolean,
    buttonLayout: AlertDialogButtonLayout,
    icon: @Composable (() -> Unit)?,
    title: @Composable (() -> Unit)?,
    content: @Composable (() -> Unit)?,
    positiveButton: @Composable (() -> Unit)?,
    negativeButton: @Composable (() -> Unit)?,
    neutralButton: @Composable (() -> Unit)?
) {
    val header: @Composable (() -> Unit)? = if (icon != null || title != null) {
        {
            val styledTitle: @Composable (() -> Unit)? = title?.let {
                {
                    CurrentTextStyleProvider(
                        MaterialTheme.typography().h6
                    ) {
                        ProvideEmphasis(
                            emphasis = MaterialTheme.emphasisLevels().high,
                            children = title
                        )
                    }
                }
            }

            val styledIcon: @Composable (() -> Unit)? = icon?.let {
                {
                    ProvideEmphasis(
                        emphasis = MaterialTheme.emphasisLevels().high,
                        children = icon
                    )
                }
            }

            if (styledIcon != null && styledTitle != null) {
                Row(
                    mainAxisAlignment = MainAxisAlignment.Start,
                    crossAxisAlignment = CrossAxisAlignment.Center
                ) {
                    styledIcon()
                    Spacer(LayoutWidth(16.dp))
                    styledTitle()
                }
            } else if (styledIcon != null) {
                styledIcon()
            } else if (styledTitle != null) {
                styledTitle()
            }
        }
    } else {
        null
    }

    val finalContent: @Composable (() -> Unit)? = if (content != null) {
        {
            CurrentTextStyleProvider(MaterialTheme.typography().subtitle1) {
                ProvideEmphasis(
                    emphasis = MaterialTheme.emphasisLevels().medium,
                    children = content
                )
            }
        }
    } else {
        null
    }

    val buttons: @Composable (() -> Unit)? = if (positiveButton != null || negativeButton != null || neutralButton != null) {
        {
            DialogButtons(
                layout = buttonLayout,
                positiveButton = positiveButton,
                negativeButton = negativeButton,
                neutralButton = neutralButton
            )
        }
    } else {
        null
    }

    DialogContentLayout(
        showTopDivider = showTopDivider,
        showBottomDivider = showBottomDivider,
        applyContentPadding = applyContentPadding,
        header = header,
        content = finalContent,
        buttons = buttons
    )
}

@Composable
private fun DialogContentLayout(
    showTopDivider: Boolean = false,
    showBottomDivider: Boolean = false,
    applyContentPadding: Boolean,
    header: @Composable (() -> Unit)?,
    content: @Composable (() -> Unit)?,
    buttons: @Composable (() -> Unit)?
) {
    val children: @Composable () -> Unit = {
        if (header != null) {
            ParentData(DialogContentSlot.Header) {
                Container(
                    alignment = Alignment.CenterLeft,
                    padding = EdgeInsets(
                        left = 24.dp,
                        top = 24.dp,
                        right = 24.dp,
                        bottom = if (buttons != null && content == null) 28.dp else 24.dp
                    ),
                    children = header
                )
            }
        }

        if (content != null) {
            if (header != null && showTopDivider) {
                ParentData(DialogContentSlot.TopDivider) {
                    DialogDivider()
                }
            }

            ParentData(DialogContentSlot.Content) {
                Container(
                    padding = EdgeInsets(
                        top = if (header == null) 24.dp else 0.dp,
                        left = if (applyContentPadding) 24.dp else 0.dp,
                        right = if (applyContentPadding) 24.dp else 0.dp,
                        bottom = if (buttons == null) 24.dp else 0.dp
                    ),
                    alignment = Alignment.TopLeft,
                    children = content
                )
            }
        }

        if (buttons != null) {
            if (content != null && showBottomDivider) {
                ParentData(DialogContentSlot.BottomDivider) {
                    DialogDivider()
                }
            }

            ParentData(DialogContentSlot.Buttons) {
                if (!showBottomDivider && content != null) {
                    Container(
                        padding = EdgeInsets(top = 28.dp),
                        children = buttons
                    )
                } else {
                    buttons()
                }
            }
        }
    }

    Layout(children = children) { measurables, constraints ->
        var childConstraints = constraints.copy(
            minWidth = constraints.maxWidth,
            minHeight = IntPx.Zero
        )

        val headerMeasureable =
            measurables.firstOrNull { it.parentData == DialogContentSlot.Header }
        val topDividerMeasureable =
            measurables.firstOrNull { it.parentData == DialogContentSlot.TopDivider }
        val contentMeasureable =
            measurables.firstOrNull { it.parentData == DialogContentSlot.Content }
        val bottomDividerMeasureable =
            measurables.firstOrNull { it.parentData == DialogContentSlot.BottomDivider }
        val buttonsMeasureable =
            measurables.firstOrNull { it.parentData == DialogContentSlot.Buttons }

        fun measureFixed(measureable: Measurable?): Placeable? {
            return if (measureable != null) {
                val placeable = measureable.measure(childConstraints)
                childConstraints = childConstraints.copy(
                    maxHeight = childConstraints.maxHeight - placeable.height
                )
                placeable
            } else {
                null
            }
        }

        val headerPlaceable = measureFixed(headerMeasureable)
        val topDividerPlaceable = measureFixed(topDividerMeasureable)
        val bottomDividerPlaceable = measureFixed(bottomDividerMeasureable)
        val buttonsPlaceable = measureFixed(buttonsMeasureable)
        val contentPlaceable = measureFixed(contentMeasureable)

        val placeables = listOfNotNull(
            headerPlaceable,
            topDividerPlaceable,
            contentPlaceable,
            bottomDividerPlaceable,
            buttonsPlaceable
        )
        val height = placeables.map { it.height }.sumBy { it.value }.ipx

        layout(width = constraints.maxWidth, height = height) {
            var offsetY = IntPx.Zero
            placeables.forEach { placeable ->
                placeable.place(IntPx.Zero, offsetY)
                offsetY += placeable.height
            }
        }
    }
}

@Composable
private fun DialogButtons(
    layout: AlertDialogButtonLayout,
    positiveButton: @Composable (() -> Unit)?,
    negativeButton: @Composable (() -> Unit)?,
    neutralButton: @Composable (() -> Unit)?
) {
    when (layout) {
        AlertDialogButtonLayout.SideBySide -> {
            Container(
                expanded = true,
                alignment = Alignment.CenterLeft,
                height = 52.dp,
                padding = EdgeInsets(all = 8.dp)
            ) {
                Row(
                    modifier = LayoutWidth.Fill,
                    mainAxisAlignment = MainAxisAlignment.Start,
                    crossAxisAlignment = CrossAxisAlignment.Center
                ) {
                    if (neutralButton != null) {
                        Container(
                            modifier = LayoutInflexible,
                            children = neutralButton
                        )
                    }

                    Spacer(LayoutFlexible(1f))

                    if (negativeButton != null) {
                        Container(
                            modifier = LayoutInflexible,
                            children = negativeButton
                        )
                    }

                    if (positiveButton != null) {
                        Container(
                            modifier = LayoutInflexible,
                            children = positiveButton
                        )
                    }
                }
            }
        }
        AlertDialogButtonLayout.Stacked -> {
            Container(
                padding = EdgeInsets(all = 8.dp),
                alignment = Alignment.CenterRight
            ) {
                Column(
                    mainAxisAlignment = MainAxisAlignment.Center,
                    crossAxisAlignment = CrossAxisAlignment.Center
                ) {
                    positiveButton?.invoke()
                    negativeButton?.invoke()
                    neutralButton?.invoke()
                }
            }
        }
    }
}

@Composable
private fun DialogDivider() {
    Divider(color = contentColor().copy(alpha = 0.12f))
}

private enum class DialogContentSlot {
    Header, TopDivider, Content, BottomDivider, Buttons
}
