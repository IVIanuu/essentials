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

package com.ivianuu.essentials.ui.compose.material.dialog

import androidx.compose.Composable
import androidx.compose.unaryPlus
import androidx.ui.core.Alignment
import androidx.ui.core.CurrentTextStyleProvider
import androidx.ui.core.Layout
import androidx.ui.core.Measurable
import androidx.ui.core.ParentData
import androidx.ui.core.Placeable
import androidx.ui.core.dp
import androidx.ui.core.gesture.PressGestureDetector
import androidx.ui.core.ipx
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.layout.Center
import androidx.ui.layout.ConstrainedBox
import androidx.ui.layout.Container
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.DpConstraints
import androidx.ui.layout.MainAxisAlignment
import androidx.ui.layout.Padding
import androidx.ui.layout.Row
import androidx.ui.layout.WidthSpacer
import androidx.ui.material.Button
import androidx.ui.material.Divider
import androidx.ui.material.TextButtonStyle
import androidx.ui.material.surface.Card
import androidx.ui.material.themeTextStyle
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.dialog.Dialog
import com.ivianuu.essentials.ui.compose.dialog.dismissDialog
import com.ivianuu.essentials.ui.compose.material.CurrentIconStyleProvider
import com.ivianuu.essentials.ui.compose.material.IconStyle
import com.ivianuu.essentials.ui.compose.material.SecondaryTextAlpha
import com.ivianuu.essentials.ui.compose.material.colorForCurrentBackground

// todo remove hardcoded values
// todo add more styleable attributes such as corner radius
// todo add stack buttons mode

@Composable
fun AlertDialog(
    dismissOnOutsideTouch: Boolean = true,
    dismissOnBackClick: Boolean = true,
    showDividers: Boolean = false,
    applyContentPadding: Boolean = true,
    icon: (@Composable() () -> Unit)? = null,
    title: (@Composable() () -> Unit)? = null,
    content: (@Composable() () -> Unit)? = null,
    buttons: (@Composable() () -> Unit)? = null
) = composable("AlertDialog") {
    Dialog(
        dismissOnOutsideTouch = dismissOnOutsideTouch,
        dismissOnBackClick = dismissOnBackClick
    ) {
        DialogFrame {
            DialogBody(
                showDividers = showDividers,
                applyContentPadding = applyContentPadding,
                icon = icon,
                title = title,
                content = content,
                buttons = buttons
            )
        }
    }
}

@Composable
private fun DialogFrame(
    children: @Composable() () -> Unit
) = composable("DialogFrame") {
    Center {
        Padding(
            left = 32.dp,
            top = 32.dp,
            right = 32.dp,
            bottom = 32.dp
        ) {
            PressGestureDetector {
                ConstrainedBox(constraints = DpConstraints(minWidth = 280.dp)) {
                    Card(
                        shape = RoundedCornerShape(size = 8.dp),
                        elevation = 24.dp
                    ) {
                        children()
                    }
                }
            }
        }
    }
}

@Composable
fun DialogContentLayout(
    showDividers: Boolean = false,
    applyContentPadding: Boolean = true,
    header: @Composable() (() -> Unit)? = null,
    content: @Composable() (() -> Unit)? = null,
    buttons: @Composable() (() -> Unit)? = null
) = composable("DialogContentLayout") {
    val children: @Composable() () -> Unit = {
        if (header != null) {
            ParentData(DialogContentLayoutType.Header) {
                Padding(
                    left = 24.dp,
                    top = 24.dp,
                    right = 24.dp,
                    bottom = if (buttons != null && content == null) 28.dp else 24.dp
                ) {
                    header()
                }
            }
        }

        if (content != null) {
            if (header != null && showDividers) {
                ParentData(DialogContentLayoutType.TopDivider) {
                    DialogDivider()
                }
            }

            ParentData(DialogContentLayoutType.Content) {
                Padding(
                    top = if (header == null) 24.dp else 0.dp,
                    left = if (applyContentPadding) 24.dp else 0.dp,
                    right = if (applyContentPadding) 24.dp else 0.dp,
                    bottom = if (buttons == null) 24.dp else 0.dp
                ) {
                    content()
                }
            }
        }

        if (buttons != null) {
            if (content != null && showDividers) {
                ParentData(DialogContentLayoutType.BottomDivider) {
                    DialogDivider()
                }
            }

            ParentData(DialogContentLayoutType.Buttons) {
                if (!showDividers && content != null) {
                    Padding(top = 28.dp) {
                        buttons()
                    }
                } else {
                    buttons()
                }
            }
        }
    }

    Layout(children = children) { measureables, constraints ->
        var childConstraints = constraints.copy(
            minWidth = constraints.maxWidth,
            minHeight = 0.ipx
        )

        val headerMeasureable =
            measureables.firstOrNull { it.parentData == DialogContentLayoutType.Header }
        val topDividerMeasureable =
            measureables.firstOrNull { it.parentData == DialogContentLayoutType.TopDivider }
        val contentMeasureable =
            measureables.firstOrNull { it.parentData == DialogContentLayoutType.Content }
        val bottomDividerMeasureable =
            measureables.firstOrNull { it.parentData == DialogContentLayoutType.BottomDivider }
        val buttonsMeasureable =
            measureables.firstOrNull { it.parentData == DialogContentLayoutType.Buttons }

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
            var offsetY = 0.ipx
            placeables.forEach {
                it.place(0.ipx, offsetY)
                offsetY += it.height
            }
        }
    }
}

private enum class DialogContentLayoutType {
    Header, TopDivider, Content, BottomDivider, Buttons
}

@Composable
private fun DialogBody(
    showDividers: Boolean = false,
    applyContentPadding: Boolean = true,
    icon: @Composable() (() -> Unit)? = null,
    title: (@Composable() () -> Unit)? = null,
    content: (@Composable() () -> Unit)? = null,
    buttons: (@Composable() () -> Unit)? = null
) = composable("DialogBody") {
    val header: (@Composable() () -> Unit)? = if (icon != null || title != null) {
        {
            val styledTitle = title?.let {
                {
                    CurrentTextStyleProvider(
                        +themeTextStyle { h6 }
                    ) {
                        title()
                    }
                }
            }

            val styledIcon = icon?.let {
                {
                    CurrentIconStyleProvider(
                        IconStyle(color = +colorForCurrentBackground())
                    ) {
                        icon()
                    }
                }
            }

            if (styledIcon != null && styledTitle != null) {
                Row(
                    mainAxisAlignment = MainAxisAlignment.Start,
                    crossAxisAlignment = CrossAxisAlignment.Center
                ) {
                    styledIcon()
                    WidthSpacer(16.dp)
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

    DialogContentLayout(
        showDividers = showDividers,
        applyContentPadding = applyContentPadding,
        header = header,
        content = content?.let {
            {
                CurrentTextStyleProvider(
                    (+themeTextStyle { subtitle1 }).copy(
                        color = (+colorForCurrentBackground()).copy(alpha = SecondaryTextAlpha)
                    )
                ) {
                    content()
                }
            }
        },
        buttons = buttons?.let {
            {
                Container(
                    expanded = true,
                    alignment = Alignment.CenterRight,
                    height = 52.dp
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
    )
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
        onClick = onClick?.let {
            {
                onClick()
                if (dismissDialogOnClick) dismissDialog()
            }
        },
        style = TextButtonStyle()
    )
}

@Composable
fun DialogCloseButton(text: String) = composable("DialogCloseButton") {
    DialogButton(
        text = text,
        dismissDialogOnClick = true,
        onClick = {})
}

@Composable
private fun DialogDivider() = composable("DialogDivider") {
    Divider(color = (+colorForCurrentBackground()).copy(alpha = 0.12f))
}