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
import androidx.compose.key
import androidx.compose.remember
import androidx.compose.state
import androidx.compose.stateFor
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Border
import androidx.ui.foundation.Box
import androidx.ui.foundation.Clickable
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.ProvideTextStyle
import androidx.ui.foundation.TextFieldValue
import androidx.ui.foundation.contentColor
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.Spacer
import androidx.ui.layout.Table
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.padding
import androidx.ui.layout.preferredHeight
import androidx.ui.layout.preferredSize
import androidx.ui.layout.preferredWidth
import androidx.ui.layout.preferredWidthIn
import androidx.ui.layout.wrapContentSize
import androidx.ui.material.MaterialTheme
import androidx.ui.material.SliderPosition
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ArrowBack
import androidx.ui.material.icons.filled.Check
import androidx.ui.res.stringResource
import androidx.ui.unit.dp
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.common.Scroller
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.core.TextField
import com.ivianuu.essentials.ui.core.hideKeyboardOnDispose
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.essentials.ui.layout.Column
import com.ivianuu.essentials.ui.layout.CrossAxisAlignment
import com.ivianuu.essentials.ui.layout.LayoutSquared
import com.ivianuu.essentials.ui.layout.MainAxisAlignment
import com.ivianuu.essentials.ui.layout.Row
import com.ivianuu.essentials.ui.material.DefaultSliderStyle
import com.ivianuu.essentials.ui.material.Slider
import com.ivianuu.essentials.ui.material.Surface
import com.ivianuu.essentials.ui.material.TextButtonStyle
import com.ivianuu.essentials.ui.material.ripple
import com.ivianuu.essentials.ui.navigation.NavigatorAmbient
import com.ivianuu.essentials.util.toColor
import com.ivianuu.essentials.util.toHexString

fun ColorPickerRoute(
    initialColor: Color,
    colorPalettes: List<ColorPickerPalette> = ColorPickerPalette.values().toList(),
    allowCustomArgb: Boolean = true,
    showAlphaSelector: Boolean = false,
    title: String? = null
) = DialogRoute {
    val navigator = NavigatorAmbient.current
    ColorPickerDialog(
        initialColor = initialColor,
        colorPalettes = colorPalettes,
        onColorSelected = { navigator.popTop(result = it) },
        allowCustomArgb = allowCustomArgb,
        showAlphaSelector = showAlphaSelector,
        dismissOnSelection = false,
        title = {
            Text(title ?: stringResource(R.string.es_color_picker_title))
        }
    )
}

@Composable
fun ColorPickerDialog(
    initialColor: Color,
    colorPalettes: List<ColorPickerPalette> = ColorPickerPalette.values().toList(),
    onColorSelected: (Color) -> Unit,
    allowCustomArgb: Boolean = true,
    showAlphaSelector: Boolean = false,
    dismissOnSelection: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null
) {
    val (currentColor, setCurrentColor) = state { initialColor }
    val (currentPage, setCurrentPage) = state { ColorPickerPage.Colors }
    val otherPage = when (currentPage) {
        ColorPickerPage.Colors -> ColorPickerPage.Editor
        ColorPickerPage.Editor -> ColorPickerPage.Colors
    }

    if (!allowCustomArgb && currentPage == ColorPickerPage.Editor) {
        setCurrentPage(ColorPickerPage.Colors)
    }

    Dialog(
        icon = icon,
        title = title,
        applyContentPadding = false,
        positiveButton = {
            ColoredDialogButton(
                color = currentColor,
                dismissOnSelection = dismissOnSelection,
                onClick = { onColorSelected(currentColor) }
            ) { Text("OK") }
        },
        negativeButton = { DialogCloseButton { Text("Cancel") } },
        neutralButton = {
            if (allowCustomArgb) {
                DialogButton(
                    dismissDialogOnClick = false,
                    onClick = { setCurrentPage(otherPage) }
                ) {
                    Text(otherPage.title)
                }
            }
        },
        content = {
            Box(
                modifier = Modifier.preferredHeight(300.dp)
                    .padding(start = 24.dp, end = 24.dp)
            ) {
                when (currentPage) {
                    ColorPickerPage.Colors -> {
                        ColorGrid(
                            currentColor = currentColor,
                            colorPalettes = colorPalettes,
                            onColorSelected = setCurrentColor
                        )
                    }
                    ColorPickerPage.Editor -> {
                        ColorEditor(
                            color = currentColor,
                            onColorChanged = setCurrentColor,
                            showAlphaSelector = showAlphaSelector
                        )
                    }
                }
            }
        }
    )
}

@Composable
private fun ColorGrid(
    currentColor: Color,
    colorPalettes: List<ColorPickerPalette>,
    onColorSelected: (Color) -> Unit
) {
    val (currentPalette, setCurrentPalette) = state<ColorPickerPalette?> { null }
    val items = remember(currentPalette) {
        currentPalette?.colors
            ?.map { ColorGridItem.Color(it) }
            ?.let { listOf(ColorGridItem.Back) + it }
            ?: colorPalettes.map { ColorGridItem.Color(it.front) }
    }

    key(currentPalette) {
        Scroller(modifier = Modifier.padding(all = 4.dp)) {
            Table(
                columns = 4,
                alignment = { Alignment.Center }
            ) {
                val chunkedItems = items.chunked(4)
                chunkedItems.forEach { rowItems ->
                    tableRow {
                        rowItems.forEach { item ->
                            key(item) {
                                when (item) {
                                    is ColorGridItem.Back -> ColorGridBackButton(
                                        onClick = { setCurrentPalette(null) }
                                    )
                                    is ColorGridItem.Color -> ColorGridItem(
                                        color = item.color,
                                        isSelected = item.color == currentColor,
                                        onClick = {
                                            if (currentPalette == null) {
                                                val paletteForItem =
                                                    colorPalettes.first { it.front == item.color }
                                                if (paletteForItem.colors.size > 1) {
                                                    setCurrentPalette(paletteForItem)
                                                } else {
                                                    onColorSelected(item.color)
                                                }
                                            } else {
                                                onColorSelected(item.color)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private sealed class ColorGridItem {
    object Back : ColorGridItem()
    data class Color(val color: androidx.ui.graphics.Color) : ColorGridItem()
}

@Composable
private fun ColorGridItem(
    onClick: () -> Unit,
    color: Color,
    isSelected: Boolean
) {
    BaseColorGridItem(onClick = onClick) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = color,
            shape = RoundedCornerShape(50),
            border = Border(
                size = 1.dp,
                color = MaterialTheme.colors.onSurface
            ),
            elevation = 0.dp
        ) {
            if (isSelected) {
                Box(modifier = Modifier.fillMaxSize(), gravity = ContentGravity.Center) {
                    Icon(
                        icon = Icons.Default.Check,
                        modifier = Modifier.preferredSize(size = 36.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorGridBackButton(
    onClick: () -> Unit,
    unused: Any? = null
) {
    BaseColorGridItem(onClick = onClick) {
        Icon(
            icon = Icons.Default.ArrowBack,
            modifier = Modifier.preferredSize(size = 36.dp)
        )
    }
}

@Composable
private fun BaseColorGridItem(
    onClick: () -> Unit,
    children: @Composable () -> Unit
) {
    Clickable(onClick = onClick, modifier = Modifier.ripple()) {
        Box(
            modifier = LayoutSquared(LayoutSquared.Fit.MatchWidth)
                .padding(all = 4.dp)
                .wrapContentSize(Alignment.Center),
            children = children
        )
    }
}

@Composable
private fun ColorEditor(
    color: Color,
    onColorChanged: (Color) -> Unit,
    showAlphaSelector: Boolean
) {
    Column {
        ColorEditorHeader(
            color = color,
            showAlphaSelector = showAlphaSelector,
            onColorChanged = onColorChanged
        )

        ColorComponent.values()
            .filter { it != ColorComponent.Alpha || showAlphaSelector }
            .forEach { component ->
                key(component) {
                    ColorComponentItem(
                        component = component,
                        value = component.extract(color),
                        onChanged = { onColorChanged(component.apply(color, it)) }
                    )
                }
            }
    }
}

@Composable
private fun ColorEditorHeader(
    color: Color,
    showAlphaSelector: Boolean,
    onColorChanged: (Color) -> Unit
) {
    ProvideTextStyle(value = MaterialTheme.typography.subtitle1) {
        Surface(color = color) {
            Box(
                modifier = Modifier.preferredHeight(72.dp)
                    .fillMaxWidth()
                    .padding(all = 8.dp),
                gravity = ContentGravity.Center
            ) {
                Row(
                    mainAxisAlignment = MainAxisAlignment.Center,
                    crossAxisAlignment = CrossAxisAlignment.Center
                ) {
                    val (hexInput, setHexInput) = stateFor(color) {
                        TextFieldValue(color.toHexString(includeAlpha = showAlphaSelector))
                    }
                    hideKeyboardOnDispose()
                    Text("#")
                    TextField(
                        value = hexInput,
                        onValueChange = { newValue ->
                            if ((showAlphaSelector && newValue.text.length > 8) ||
                                (!showAlphaSelector && newValue.text.length > 6)
                            ) return@TextField

                            setHexInput(newValue)

                            if ((showAlphaSelector && newValue.text.length < 8) ||
                                (!showAlphaSelector && newValue.text.length < 6)
                            ) return@TextField

                            val newColor = try {
                                newValue.text.toColor()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                null
                            }

                            if (newColor != null) onColorChanged(newColor)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ColoredDialogButton(
    onClick: () -> Unit,
    color: Color,
    dismissOnSelection: Boolean,
    children: @Composable () -> Unit
) {
    DialogButton(
        onClick = onClick,
        dismissDialogOnClick = dismissOnSelection,
        style = TextButtonStyle(contentColor = color),
        children = children
    )
}

@Composable
private fun ColorComponentItem(
    component: ColorComponent,
    value: Float,
    onChanged: (Float) -> Unit
) {
    Row(
        modifier = Modifier.preferredHeight(48.dp).fillMaxWidth(),
        crossAxisAlignment = CrossAxisAlignment.Center
    ) {
        Text(
            text = component.title,
            textStyle = MaterialTheme.typography.subtitle1
        )

        Spacer(Modifier.preferredWidth(8.dp))

        val position = SliderPosition(initial = value)

        Slider(
            position = position,
            modifier = LayoutFlexible(flex = 1f),
            onValueChange = {
                position.value = it
                onChanged(it)
            },
            style = DefaultSliderStyle(color = component.color())
        )

        Spacer(Modifier.preferredWidth(8.dp))

        Text(
            text = (255 * value).toInt().toString(),
            modifier = Modifier.preferredWidthIn(minWidth = 56.dp),
            textStyle = MaterialTheme.typography.subtitle1
        )
    }
}

private enum class ColorComponent(
    val title: String,
    val color: @Composable () -> Color
) {
    Alpha(
        title = "A",
        color = { contentColor() }
    ) {
        override fun extract(color: Color) = color.alpha
        override fun apply(color: Color, value: Float) = color.copy(alpha = value)
    },
    Red(
        title = "R",
        color = { Color.Red }
    ) {
        override fun extract(color: Color) = color.red
        override fun apply(color: Color, value: Float) = color.copy(red = value)
    },
    Green(
        title = "G",
        color = { Color.Green }
    ) {
        override fun extract(color: Color) = color.green
        override fun apply(color: Color, value: Float) = color.copy(green = value)
    },
    Blue(
        title = "B",
        color = { Color.Blue }
    ) {
        override fun extract(color: Color) = color.blue
        override fun apply(color: Color, value: Float) = color.copy(blue = value)
    };

    abstract fun extract(color: Color): Float
    abstract fun apply(color: Color, value: Float): Color
}

// todo title convert to resource
private enum class ColorPickerPage(
    val title: String
) {
    Colors("Colors"), Editor("Custom")
}
