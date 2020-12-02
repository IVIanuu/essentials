/*
 * Copyright 2020 Manuel Wrage
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

import androidx.compose.foundation.BorderStroke
import androidx.compose.material.Icon
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredWidthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AmbientContentColor
import androidx.compose.material.ButtonConstants
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.ripple.rememberRippleIndication
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMap
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.ui.animatedstack.AnimatedBox
import com.ivianuu.essentials.ui.animatedstack.animation.FadeStackTransition
import com.ivianuu.essentials.ui.core.rememberState
import com.ivianuu.essentials.ui.core.toColorOrNull
import com.ivianuu.essentials.ui.core.toHexString
import com.ivianuu.essentials.ui.layout.SquareFit
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.layout.squared
import com.ivianuu.essentials.ui.material.Slider
import com.ivianuu.essentials.ui.material.guessingContentColorFor

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
    var currentColor by rememberState { initialColor }
    var currentPage by rememberState { ColorPickerPage.Colors }
    val otherPage = when (currentPage) {
        ColorPickerPage.Colors -> ColorPickerPage.Editor
        ColorPickerPage.Editor -> ColorPickerPage.Colors
    }

    if (!allowCustomArgb && currentPage == ColorPickerPage.Editor) {
        currentPage = ColorPickerPage.Colors
    }

    Dialog(
        modifier = modifier,
        icon = icon,
        title = title,
        applyContentPadding = false,
        positiveButton = {
            TextButton(
                onClick = { onColorSelected(currentColor) },
                colors = ButtonConstants.defaultTextButtonColors(
                    contentColor = currentColor
                )
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
                    onClick = { currentPage = otherPage },
                    colors = ButtonConstants.defaultTextButtonColors(
                        contentColor = currentColor
                    )
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
                            colors = colorPalettes,
                            onColorSelected = { currentColor = it }
                        )
                    }
                    ColorPickerPage.Editor -> {
                        ColorEditor(
                            modifier = Modifier.fillMaxSize(),
                            color = currentColor,
                            onColorChanged = { currentColor = it },
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
    colors: List<ColorPickerPalette>,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentPalette by rememberState<ColorPickerPalette?> { null }
    AnimatedBox(current = currentPalette) { palette ->
        val items = remember {
            palette
                ?.colors
                ?.map { ColorGridItem.Color(it) }
                ?.let { listOf(ColorGridItem.Back) + it }
                ?: colors.map { ColorGridItem.Color(it.front) }
        }

        WithConstraints(modifier = modifier) {
            ScrollableColumn(
                modifier = Modifier.padding(all = 4.dp)
            ) {
                items.chunked(4).forEach { rowItems ->
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        rowItems.forEach { item ->
                            key(item) {
                                Box(
                                    modifier = Modifier.size(maxWidth / 4),
                                    contentAlignment = Alignment.Center
                                ) {
                                    when (item) {
                                        is ColorGridItem.Back -> ColorGridBackButton(
                                            onClick = { currentPalette = null }
                                        )
                                        is ColorGridItem.Color -> ColorGridItem(
                                            color = item.color,
                                            isSelected = item.color == currentColor,
                                            onClick = {
                                                if (palette == null) {
                                                    val paletteForItem =
                                                        colors.first { it.front == item.color }
                                                    if (paletteForItem.colors.size > 1) {
                                                        currentPalette = paletteForItem
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
    data class Color(val color: androidx.compose.ui.graphics.Color) : ColorGridItem()
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
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colors.onSurface
            ),
            elevation = 0.dp
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
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
            imageVector = Icons.Default.ArrowBack,
            modifier = Modifier.size(size = 36.dp)
        )
    }
}

@Composable
private fun BaseColorGridItem(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier.squared(SquareFit.MatchWidth)
            .padding(all = 4.dp)
            .wrapContentSize(Alignment.Center)
            .clickable(onClick = onClick, indication = rememberRippleIndication(bounded = false)),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
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
) = key(color) {
    var hexInput by rememberState { color.toHexString(includeAlpha = showAlphaSelector) }
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(8.dp),
        visualTransformation = object : VisualTransformation {
            override fun filter(text: AnnotatedString): TransformedText {
                return TransformedText(
                    AnnotatedString("#") + text,
                    object : OffsetMap {
                        override fun originalToTransformed(offset: Int): Int {
                            return offset + 1
                        }

                        override fun transformedToOriginal(offset: Int): Int {
                            return offset - 1
                        }
                    }
                )
            }
        },
        backgroundColor = color,
        activeColor = guessingContentColorFor(color),
        inactiveColor = guessingContentColorFor(color),
        textStyle =  MaterialTheme.typography.subtitle1
            .copy(color = guessingContentColorFor(color)),
        value = hexInput,
        onValueChange = { newValue ->
            if ((showAlphaSelector && newValue.length > 8) ||
                (!showAlphaSelector && newValue.length > 6)
            ) return@TextField

            hexInput = newValue

            if ((showAlphaSelector && newValue.length < 8) ||
                (!showAlphaSelector && newValue.length < 6)
            ) return@TextField

            val newColor = newValue.toColorOrNull()

            if (newColor != null) onColorChanged(newColor)
        }
    )
}

@Composable
private fun ColorComponentItem(
    component: ColorComponent,
    value: Float,
    onValueChanged: (Float) -> Unit
) {
    Row(
        modifier = Modifier.height(48.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = component.title,
            style = MaterialTheme.typography.subtitle1
        )

        MaterialTheme(
            colors = MaterialTheme.colors.copy(
                primary = component.color()
            ),
            typography = MaterialTheme.typography,
            shapes = MaterialTheme.shapes
        ) {
            Slider(
                value = value,
                onValueChange = onValueChanged,
                modifier = Modifier.padding(horizontal = 8.dp)
                    .weight(1f)
            )
        }

        Text(
            text = (255 * value).toInt().toString(),
            modifier = Modifier.preferredWidthIn(min = 56.dp),
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
        color = { AmbientContentColor.current }
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
