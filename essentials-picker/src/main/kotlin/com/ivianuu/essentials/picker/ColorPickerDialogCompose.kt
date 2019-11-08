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

package com.ivianuu.essentials.picker

import androidx.compose.Composable
import androidx.compose.Effect
import androidx.compose.ambient
import androidx.compose.effectOf
import androidx.compose.memo
import androidx.compose.state
import androidx.compose.unaryPlus
import androidx.ui.core.Alignment
import androidx.ui.core.Draw
import androidx.ui.core.PxSize
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.engine.geometry.Offset
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.ColoredRect
import androidx.ui.foundation.VerticalScroller
import androidx.ui.graphics.Canvas
import androidx.ui.graphics.Color
import androidx.ui.graphics.Paint
import androidx.ui.layout.Column
import androidx.ui.layout.Container
import androidx.ui.layout.CrossAxisAlignment
import androidx.ui.layout.ExpandedWidth
import androidx.ui.layout.FlexRow
import androidx.ui.layout.Padding
import androidx.ui.layout.Stack
import androidx.ui.layout.Table
import androidx.ui.material.Colors
import androidx.ui.material.MaterialTheme
import androidx.ui.material.TextButtonStyle
import androidx.ui.material.Typography
import androidx.ui.material.ripple.Ripple
import androidx.ui.material.themeTextStyle
import com.ivianuu.essentials.ui.compose.core.composable
import com.ivianuu.essentials.ui.compose.dialog.DialogButton
import com.ivianuu.essentials.ui.compose.dialog.DialogCloseButton
import com.ivianuu.essentials.ui.compose.dialog.MaterialDialog
import com.ivianuu.essentials.ui.compose.layout.Expand
import com.ivianuu.essentials.ui.compose.layout.TightColumn
import com.ivianuu.essentials.ui.compose.layout.WidthFitSquare
import com.ivianuu.essentials.ui.compose.material.Slider
import com.ivianuu.essentials.ui.compose.material.Tab
import com.ivianuu.essentials.ui.compose.material.TabContent
import com.ivianuu.essentials.ui.compose.material.TabController
import com.ivianuu.essentials.ui.compose.material.TabRow
import com.ivianuu.essentials.ui.compose.material.colorForBackground
import com.ivianuu.essentials.ui.compose.material.colorForCurrentBackground

@Composable
fun ColorPickerDialog(
    colors: List<Color> = PrimaryMaterialColors,
    onColorSelected: (Color) -> Unit,
    initialColor: Color,
    allowCustomArgb: Boolean = true,
    showAlphaSelector: Boolean = false,
    icon: (@Composable() () -> Unit)? = null,
    title: (@Composable() () -> Unit)? = null
) = composable("ColorPickerDialog") {
    val (currentColor, setCurrentColor) = +state { initialColor }

    MaterialDialog(
        icon = icon,
        title = title,
        applyContentPadding = false,
        positiveButton = {
            ColoredDialogButton(
                text = "OK",
                color = currentColor,
                onClick = { onColorSelected(currentColor) }
            )
        },
        negativeButton = { DialogCloseButton(text = "Cancel") },
        content = {
            ColorPickerContent(
                colors = colors,
                allowCustomArgb = allowCustomArgb,
                showAlphaSelector = showAlphaSelector,
                color = currentColor,
                onColorChanged = setCurrentColor
            )
        }
    )
}

@Composable
private fun ColorPickerContent(
    colors: List<Color>,
    allowCustomArgb: Boolean,
    showAlphaSelector: Boolean,
    color: Color,
    onColorChanged: (Color) -> Unit
) = composable("ColorPickerContent") {
    if (allowCustomArgb) {
        TabController(items = ColorPickerPage.values().toList()) {
            TightColumn {
                val currentColors = +ambient(Colors)

                MaterialTheme(
                    colors = currentColors.copy(
                        primary = currentColors.surface,
                        onPrimary = currentColors.onSurface
                    ),
                    typography = +ambient(Typography)
                ) {
                    TabRow<ColorPickerPage> { _, page ->
                        Tab(text = page.title)
                    }
                }

                Padding(
                    left = 24.dp,
                    right = 24.dp
                ) {
                    TabContent<ColorPickerPage> { _, page ->
                        Container(height = 300.dp) {
                            when (page) {
                                ColorPickerPage.Colors -> {
                                    ColorGrid(
                                        colors = colors,
                                        onColorSelected = onColorChanged
                                    )
                                }
                                ColorPickerPage.Editor -> {
                                    ColorEditor(
                                        color = color,
                                        onColorChanged = onColorChanged,
                                        showAlphaSelector = showAlphaSelector
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        Padding(
            left = 24.dp,
            right = 24.dp
        ) {
            ColorGrid(colors = colors, onColorSelected = onColorChanged)
        }
    }
}

@Composable
private fun ColorGrid(
    colors: List<Color>,
    onColorSelected: (Color) -> Unit
) = composable("ColorGrid") {
    VerticalScroller {
        Padding(padding = 4.dp) {
            Table(
                columns = 4,
                alignment = { Alignment.Center }
            ) {
                val chunkedColors = colors.chunked(4)
                chunkedColors.forEachIndexed { index, rowColors ->
                    tableRow {
                        rowColors.forEach { color ->
                            composable(color) {
                                ColorGridItem(color = color, onClick = {
                                    onColorSelected(color)
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorGridItem(
    color: Color,
    onClick: () -> Unit
) = composable("ColorGridItem") {
    Padding(padding = 4.dp) {
        Ripple(bounded = false) {
            Clickable(onClick = onClick) {
                WidthFitSquare {
                    val paint = +memo { Paint() }
                    paint.color = color
                    Expand {
                        Draw { canvas: Canvas, parentSize: PxSize ->
                            canvas.drawCircle(
                                Offset(parentSize.width.value / 2, parentSize.height.value / 2),
                                parentSize.width.value / 2,
                                paint
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorEditor(
    color: Color,
    onColorChanged: (Color) -> Unit,
    showAlphaSelector: Boolean
) = composable("ColorEditor") {
    Column {
        Container(
            height = 72.dp,
            modifier = ExpandedWidth
        ) {
            // todo use surface once fixed
            Stack {
                ColoredRect(color)
                Text(
                    text = color.toString(),
                    style = (+themeTextStyle { subtitle1 }).copy(
                        color = +colorForBackground(color)
                    )
                )
            }
        }

        ColorComponent.values()
            .filter {
                it != ColorComponent.Alpha || showAlphaSelector
            }
            .forEach { component ->
                composable(component) {
                    ColorComponentItem(
                        component = component,
                        value = component.read(color),
                        onChanged = { onColorChanged(component.apply(color, it)) }
                    )
                }
            }
    }
}

@Composable
private fun ColoredDialogButton(
    text: String,
    color: Color,
    onClick: () -> Unit
) = composable("ColoredDialogButton") {
    DialogButton(
        text = text,
        style = TextButtonStyle(contentColor = color),
        onClick = onClick
    )
}

@Composable
private fun ColorComponentItem(
    component: ColorComponent,
    value: Float,
    onChanged: (Float) -> Unit
) = composable("ColorEditorItem") {
    Container(
        height = 48.dp,
        modifier = ExpandedWidth
    ) {
        FlexRow(
            crossAxisAlignment = CrossAxisAlignment.Center
        ) {
            inflexible {
                Text(
                    text = component.title,
                    style = +themeTextStyle { subtitle1 }
                )
            }

            flexible(1f) {
                Slider(
                    value = (255 * value).toInt(),
                    min = 0,
                    max = 255,
                    onChanged = { onChanged(it / 255f) },
                    color = +component.color
                )
            }

            inflexible {
                Text(
                    text = (255 * value).toInt().toString(),
                    style = +themeTextStyle { subtitle1 }
                )
            }
        }
    }
}

private enum class ColorComponent(
    val title: String,
    val color: Effect<Color>
) {
    Alpha(
        title = "A",
        color = effectOf { +colorForCurrentBackground() }
    ) {
        override fun read(color: Color) = color.alpha
        override fun apply(color: Color, value: Float) = color.copy(alpha = value)
    },
    Red(
        title = "R",
        color = effectOf { Color.Red }
    ) {
        override fun read(color: Color) = color.red
        override fun apply(color: Color, value: Float) = color.copy(red = value)
    },
    Green(
        title = "G",
        color = effectOf { Color.Green }
    ) {
        override fun read(color: Color) = color.green
        override fun apply(color: Color, value: Float) = color.copy(green = value)
    },
    Blue(
        title = "B",
        color = effectOf { Color.Blue }
    ) {
        override fun read(color: Color) = color.blue
        override fun apply(color: Color, value: Float) = color.copy(blue = value)
    };

    abstract fun read(color: Color): Float
    abstract fun apply(color: Color, value: Float): Color
}

// todo title convert to resource
private enum class ColorPickerPage(
    val title: String
) {
    Colors("Colors"), Editor("Color Editor")
}