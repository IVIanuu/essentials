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
import androidx.ui.core.Layout
import androidx.ui.core.Measurable
import androidx.ui.core.Modifier
import androidx.ui.core.Placeable
import androidx.ui.core.tag
import androidx.ui.foundation.Box
import androidx.ui.foundation.ProvideTextStyle
import androidx.ui.layout.Arrangement
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.Spacer
import androidx.ui.layout.Stack
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredWidth
import androidx.ui.material.EmphasisAmbient
import androidx.ui.material.MaterialTheme
import androidx.ui.material.ProvideEmphasis
import androidx.ui.unit.dp
import androidx.ui.unit.ipx
import com.ivianuu.essentials.ui.material.HorizontalDivider

@Composable
fun Dialog(
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
    BaseDialog {
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
                    ProvideTextStyle(MaterialTheme.typography.h6) {
                        ProvideEmphasis(
                            emphasis = EmphasisAmbient.current.high,
                            content = title
                        )
                    }
                }
            }

            val styledIcon: @Composable (() -> Unit)? = icon?.let {
                {
                    ProvideEmphasis(
                        emphasis = EmphasisAmbient.current.high,
                        content = icon
                    )
                }
            }

            if (styledIcon != null && styledTitle != null) {
                Row(verticalGravity = Alignment.CenterVertically) {
                    styledIcon()
                    Spacer(Modifier.preferredWidth(16.dp))
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
            ProvideTextStyle(MaterialTheme.typography.subtitle1) {
                ProvideEmphasis(
                    emphasis = EmphasisAmbient.current.medium,
                    content = content
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
            Stack(
                modifier = Modifier.padding(
                    start = 24.dp,
                    top = 24.dp,
                    end = 24.dp,
                    bottom = if (buttons != null && content == null) 28.dp else 24.dp
                ).tag(DialogContentSlot.Header)
            ) { header() }
        }

        if (content != null) {
            if (header != null && showTopDivider) {
                HorizontalDivider(modifier = Modifier.tag(DialogContentSlot.TopDivider))
            }

            Stack(
                modifier = Modifier.padding(
                    start = if (applyContentPadding) 24.dp else 0.dp,
                    top = if (header == null) 24.dp else 0.dp,
                    end = if (applyContentPadding) 24.dp else 0.dp,
                    bottom = if (buttons == null) 24.dp else 0.dp
                ).tag(DialogContentSlot.Content)
            ) { content() }
        }

        if (buttons != null) {
            if (content != null && showBottomDivider) {
                HorizontalDivider(modifier = Modifier.tag(DialogContentSlot.BottomDivider))
            }

            if (!showBottomDivider && content != null) {
                Box(
                    modifier = Modifier.padding(top = 28.dp)
                        .tag(DialogContentSlot.Buttons),
                    children = buttons
                )
            } else {
                Box(
                    modifier = Modifier.tag(DialogContentSlot.Buttons),
                    children = buttons
                )
            }
        }
    }

    Layout(children = children) { measurables, constraints, _ ->
        var childConstraints = constraints.copy(
            minWidth = constraints.maxWidth,
            minHeight = 0.ipx
        )

        val headerMeasureable =
            measurables.firstOrNull { it.tag == DialogContentSlot.Header }
        val topDividerMeasureable =
            measurables.firstOrNull { it.tag == DialogContentSlot.TopDivider }
        val contentMeasureable =
            measurables.firstOrNull { it.tag == DialogContentSlot.Content }
        val bottomDividerMeasureable =
            measurables.firstOrNull { it.tag == DialogContentSlot.BottomDivider }
        val buttonsMeasureable =
            measurables.firstOrNull { it.tag == DialogContentSlot.Buttons }

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
            placeables.forEach { placeable ->
                placeable.place(0.ipx, offsetY)
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
            Row(
                modifier = Modifier
                    .preferredHeight(52.dp)
                    .fillMaxWidth()
                    .padding(all = 8.dp),
                verticalGravity = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                neutralButton?.invoke()
                Spacer(Modifier.weight(1f))
                negativeButton?.invoke()
                positiveButton?.invoke()
            }
        }
        AlertDialogButtonLayout.Stacked -> {
            Column(
                modifier = Modifier.fillMaxWidth().padding(all = 8.dp),
                verticalArrangement = Arrangement.Center,
                horizontalGravity = Alignment.End
            ) {
                positiveButton?.invoke()
                negativeButton?.invoke()
                neutralButton?.invoke()
            }
        }
    }
}

private enum class DialogContentSlot {
    Header, TopDivider, Content, BottomDivider, Buttons
}
