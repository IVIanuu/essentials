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
import androidx.compose.getValue
import androidx.compose.key
import androidx.compose.remember
import androidx.compose.setValue
import androidx.compose.state
import androidx.compose.stateFor
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.core.WithConstraints
import androidx.ui.foundation.Border
import androidx.ui.foundation.Box
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.ProvideTextStyle
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.foundation.clickable
import androidx.ui.foundation.contentColor
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.Color
import androidx.ui.layout.Arrangement
import androidx.ui.layout.Column
import androidx.ui.layout.Row
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.height
import androidx.ui.layout.padding
import androidx.ui.layout.preferredWidthIn
import androidx.ui.layout.size
import androidx.ui.layout.wrapContentSize
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Slider
import androidx.ui.material.TextButton
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.ArrowBack
import androidx.ui.material.icons.filled.Check
import androidx.ui.material.ripple.RippleIndication
import androidx.ui.res.stringResource
import androidx.ui.unit.dp
import com.ivianuu.essentials.R
import com.ivianuu.essentials.ui.animatedstack.AnimatedBox
import com.ivianuu.essentials.ui.animatedstack.animation.FadeStackTransition
import com.ivianuu.essentials.ui.core.TextField
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.essentials.ui.layout.SquareFit
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.layout.squared
import com.ivianuu.essentials.ui.material.Surface
import com.ivianuu.essentials.ui.navigation.DialogRoute
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
        onCancel = { navigator.popTop() },
        allowCustomArgb = allowCustomArgb,
        showAlphaSelector = showAlphaSelector,
        title = { Text(title ?: stringResource(R.string.es_color_picker_title)) }
    )
}

@Composable
fun ColorPickerDialog(
    initialColor: Color,
    colorPalettes: List<ColorPickerPalette> = ColorPickerPalette.values().toList(),
    onColorSelected: (Color) -> Unit,
    onCancel: () -> Unit,
    allowCustomArgb: Boolean = true,
    showAlphaSelector: Boolean = false,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
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
        modifier = modifier,
        icon = icon,
        title = title,
        applyContentPadding = false,
        positiveButton = {
            TextButton(
                onClick = { onColorSelected(currentColor) },
                contentColor = currentColor
            ) {
                Text("OK")
            }
        },
        negativeButton = {
            TextButton(onClick = onCancel) { Text("Cancel") }
        },
        neutralButton = {
            if (allowCustomArgb) {
                TextButton(
                    onClick = {
                        setCurrentPage(otherPage)
                    },
                    contentColor = currentColor
                ) {
                    Text(otherPage.title)
                }
            }
        },
        content = {
            AnimatedBox(
                modifier = Modifier.height(300.dp)
                    .padding(start = 24.dp, end = 24.dp),
                current = currentPage,
                transition = FadeStackTransition()
            ) { currentPage ->
                when (currentPage) {
                    ColorPickerPage.Colors -> {
                        ColorGrid(
                            modifier = Modifier.fillMaxSize(),
                            currentColor = currentColor,
                            colorPalettes = colorPalettes,
                            onColorSelected = setCurrentColor
                        )
                    }
                    ColorPickerPage.Editor -> {
                        ColorEditor(
                            modifier = Modifier.fillMaxSize(),
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
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentPaletteState = state<ColorPickerPalette?> { null }
    AnimatedBox(current = currentPaletteState.value) { palette ->
        val items = remember {
            palette
                ?.colors
                ?.map { ColorGridItem.Color(it) }
                ?.let { listOf(ColorGridItem.Back) + it }
                ?: colorPalettes.map { ColorGridItem.Color(it.front) }
        }

        WithConstraints(modifier = modifier) {
            VerticalScroller(modifier = Modifier.padding(all = 4.dp)) {
                items.chunked(4).forEach { rowItems ->
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalGravity = Alignment.CenterVertically
                    ) {
                        rowItems.forEach { item ->
                            key(item) {
                                Box(
                                    modifier = Modifier.size(maxWidth / 4),
                                    gravity = ContentGravity.Center
                                ) {
                                    when (item) {
                                        is ColorGridItem.Back -> ColorGridBackButton(
                                            onClick = { currentPaletteState.value = null }
                                        )
                                        is ColorGridItem.Color -> ColorGridItem(
                                            color = item.color,
                                            isSelected = item.color == currentColor,
                                            onClick = {
                                                if (palette == null) {
                                                    val paletteForItem =
                                                        colorPalettes.first { it.front == item.color }
                                                    if (paletteForItem.colors.size > 1) {
                                                        currentPaletteState.value = paletteForItem
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
                Icon(
                    icon = Icons.Default.Check,
                    modifier = Modifier
                        .center()
                        .size(size = 36.dp)
                )
            }
        }
    }
}

@Composable
private fun ColorGridBackButton(onClick: () -> Unit) {
    BaseColorGridItem(onClick = onClick) {
        Icon(
            icon = Icons.Default.ArrowBack,
            modifier = Modifier.size(size = 36.dp)
        )
    }
}

@Composable
private fun BaseColorGridItem(
    onClick: () -> Unit,
    children: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.squared(SquareFit.MatchWidth)
            .padding(all = 4.dp)
            .wrapContentSize(Alignment.Center)
            .clickable(onClick = onClick, indication = RippleIndication(bounded = false)),
        gravity = ContentGravity.Center,
        children = children
    )
}

@Composable
private fun ColorEditor(
    color: Color,
    modifier: Modifier = Modifier,
    onColorChanged: (Color) -> Unit,
    showAlphaSelector: Boolean
) {
    Column(modifier = modifier) {
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
                        onValueChanged = { onColorChanged(component.apply(color, it)) }
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
            Row(
                modifier = Modifier.height(72.dp)
                    .fillMaxWidth()
                    .padding(all = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalGravity = Alignment.CenterVertically
            ) {
                var hexInput by stateFor(color) {
                    color.toHexString(includeAlpha = showAlphaSelector)
                }
                Text("#")
                TextField(
                    value = hexInput,
                    onValueChange = { newValue ->
                        if ((showAlphaSelector && newValue.length > 8) ||
                            (!showAlphaSelector && newValue.length > 6)
                        ) return@TextField

                        hexInput = newValue

                        if ((showAlphaSelector && newValue.length < 8) ||
                            (!showAlphaSelector && newValue.length < 6)
                        ) return@TextField

                        val newColor = try {
                            newValue.toColor()
                        } catch (e: Exception) {
                            null
                        }

                        if (newColor != null) onColorChanged(newColor)
                    }
                )
            }
        }
    }
}

@Composable
private fun ColorComponentItem(
    component: ColorComponent,
    value: Float,
    onValueChanged: (Float) -> Unit
) {
    Row(
        modifier = Modifier.height(48.dp).fillMaxWidth(),
        verticalGravity = Alignment.CenterVertically
    ) {
        Text(
            text = component.title,
            style = MaterialTheme.typography.subtitle1
        )

        Slider(
            value = value,
            onValueChange = onValueChanged,
            color = component.color(),
            modifier = Modifier.padding(horizontal = 8.dp)
                .weight(1f)
        )

        Text(
            text = (255 * value).toInt().toString(),
            modifier = Modifier.preferredWidthIn(minWidth = 56.dp),
            style = MaterialTheme.typography.subtitle1
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
