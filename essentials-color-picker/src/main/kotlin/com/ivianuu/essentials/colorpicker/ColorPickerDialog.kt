/*
 * Copyright 2021 Manuel Wrage
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

package com.ivianuu.essentials.colorpicker

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.essentials.ui.animation.transition.*
import com.ivianuu.essentials.ui.core.*
import com.ivianuu.essentials.ui.dialog.*
import com.ivianuu.essentials.ui.layout.*
import com.ivianuu.essentials.ui.material.Slider
import com.ivianuu.essentials.ui.material.guessingContentColorFor

@Composable fun ColorPickerDialog(
  modifier: Modifier = Modifier,
  initialColor: Color,
  colorPalettes: List<ColorPickerPalette> = ColorPickerPalette.values().toList(),
  icon: @Composable (() -> Unit)? = null,
  title: @Composable (() -> Unit)? = null,
  onColorSelected: (Color) -> Unit,
  onCancel: () -> Unit,
  allowCustomArgb: Boolean = true,
  showAlphaSelector: Boolean = false,
) {
  var currentColor by remember { mutableStateOf(initialColor) }
  var currentScreen by remember { mutableStateOf(ColorPickerTab.COLORS) }
  val otherScreen = when (currentScreen) {
    ColorPickerTab.COLORS -> ColorPickerTab.EDITOR
    ColorPickerTab.EDITOR -> ColorPickerTab.COLORS
  }

  SideEffect {
    if (!allowCustomArgb && currentScreen == ColorPickerTab.EDITOR) {
      currentScreen = ColorPickerTab.COLORS
    }
  }

  Dialog(
    modifier = modifier,
    applyContentPadding = false,
    icon = icon,
    title = title,
    content = {
      AnimatedBox(
        modifier = Modifier.height(300.dp)
          .padding(start = 24.dp, end = 24.dp),
        current = currentScreen,
        transition = VerticalSharedAxisStackTransition()
      ) { currentScreen ->
        when (currentScreen) {
          ColorPickerTab.COLORS -> {
            ColorGrid(
              modifier = Modifier.fillMaxSize(),
              currentColor = currentColor,
              colors = colorPalettes,
              onColorSelected = { currentColor = it }
            )
          }
          ColorPickerTab.EDITOR -> {
            ColorEditor(
              modifier = Modifier.fillMaxSize(),
              color = currentColor,
              onColorChanged = { currentColor = it },
              showAlphaSelector = showAlphaSelector
            )
          }
        }
      }
    },
    buttons = {
      if (allowCustomArgb) {
        TextButton(
          onClick = { currentScreen = otherScreen },
          colors = ButtonDefaults.textButtonColors(
            contentColor = currentColor
          )
        ) {
          Text(stringResource(otherScreen.titleRes))
        }
      }

      TextButton(onClick = onCancel) { Text(stringResource(R.string.es_cancel)) }

      TextButton(
        onClick = { onColorSelected(currentColor) },
        colors = ButtonDefaults.textButtonColors(
          contentColor = currentColor
        )
      ) {
        Text(stringResource(R.string.es_ok))
      }
    }
  )
}

@Composable private fun ColorGrid(
  currentColor: Color,
  colors: List<ColorPickerPalette>,
  onColorSelected: (Color) -> Unit,
  modifier: Modifier = Modifier
) {
  var palettesStack by remember { mutableStateOf<List<ColorPickerPalette?>>(listOf(null)) }
  AnimatedStack(
    items = palettesStack,
    transition = HorizontalSharedAxisStackTransition()
  ) { palette ->
    val items = remember {
      palette
        ?.colors
        ?.map { ColorGridItem.Color(it) }
        ?.let { listOf(ColorGridItem.Back) + it }
        ?: colors.map { ColorGridItem.Color(it.front) }
    }

    BoxWithConstraints(
      modifier = Modifier.padding(all = 4.dp)
        .then(modifier)
    ) {
      LazyColumn {
        items.chunked(4).forEach { rowItems ->
          item {
            ColorGridRow(
              items = rowItems,
              currentColor = currentColor,
              maxWidth = this@BoxWithConstraints.maxWidth,
              onItemClick = { item ->
                when (item) {
                  ColorGridItem.Back -> palettesStack = listOf(null)
                  is ColorGridItem.Color -> {
                    if (palette == null) {
                      val paletteForItem =
                        colors.first { it.front == item.color }
                      if (paletteForItem.colors.size > 1) {
                        palettesStack += paletteForItem
                      } else {
                        onColorSelected(item.color)
                      }
                    } else {
                      onColorSelected(item.color)
                    }
                  }
                }
              }
            )
          }
        }
      }
    }
  }
}

@Composable private fun ColorGridRow(
  items: List<ColorGridItem>,
  onItemClick: (ColorGridItem) -> Unit,
  currentColor: Color,
  maxWidth: Dp,
) {
  Row(
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
  ) {
    items.forEach { item ->
      key(item) {
        Box(
          modifier = Modifier.size(maxWidth / 4),
          contentAlignment = Alignment.Center
        ) {
          when (item) {
            is ColorGridItem.Back -> ColorGridBackButton(
              onClick = { onItemClick(item) }
            )
            is ColorGridItem.Color -> ColorGridItem(
              color = item.color,
              isSelected = item.color == currentColor,
              onClick = { onItemClick(item) }
            )
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

@Composable private fun ColorGridItem(
  onClick: () -> Unit,
  color: Color,
  isSelected: Boolean
) {
  BaseColorGridItem(onClick = onClick) {
    val shape = RoundedCornerShape(50)
    Box(
      modifier = Modifier.fillMaxSize()
        .background(color, shape)
        .border(
          BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colors.onSurface
          ),
          shape
        )
    ) {
      if (isSelected) {
        Icon(
          imageVector = Icons.Default.Check,
          contentDescription = null,
          modifier = Modifier
            .center()
            .size(size = 36.dp)
        )
      }
    }
  }
}

@Composable private fun ColorGridBackButton(onClick: () -> Unit) {
  BaseColorGridItem(onClick = onClick) {
    Icon(
      imageVector = Icons.Default.ArrowBack,
      contentDescription = null,
      modifier = Modifier.size(size = 36.dp)
    )
  }
}

@Composable private fun BaseColorGridItem(
  onClick: () -> Unit,
  content: @Composable () -> Unit
) {
  Box(
    modifier = Modifier
      .squared(SquareFit.FIT_WIDTH)
      .padding(all = 4.dp)
      .wrapContentSize(Alignment.Center)
      .clickable(
        onClick = onClick,
        interactionSource = remember { MutableInteractionSource() },
        indication = rememberRipple(bounded = false)
      ),
    contentAlignment = Alignment.Center
  ) {
    content()
  }
}

@Composable private fun ColorEditor(
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
      .filter { it != ColorComponent.ALPHA || showAlphaSelector }
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

@Composable private fun ColorEditorHeader(
  color: Color,
  showAlphaSelector: Boolean,
  onColorChanged: (Color) -> Unit
) = key(color) {
  var hexInput by remember { mutableStateOf(color.toHexString(includeAlpha = showAlphaSelector)) }
  TextField(
    modifier = Modifier
      .fillMaxWidth()
      .height(100.dp)
      .padding(8.dp),
    visualTransformation = { text ->
      TransformedText(
        AnnotatedString("#") + text,
        object : OffsetMapping {
          override fun originalToTransformed(offset: Int): Int = offset + 1

          override fun transformedToOriginal(offset: Int): Int = offset - 1
        }
      )
    },
    colors = TextFieldDefaults.textFieldColors(
      backgroundColor = color,
      //   activeColor = guessingContentColorFor(color),
      //        inactiveColor = guessingContentColorFor(color),
    ),
    textStyle = MaterialTheme.typography.subtitle1
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

@Composable private fun ColorComponentItem(
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
      modifier = Modifier.widthIn(min = 56.dp),
      style = MaterialTheme.typography.subtitle1
    )
  }
}

private enum class ColorComponent(
  val title: String,
  val color: @Composable () -> Color
) {
  ALPHA(
    title = "A",
    color = { LocalContentColor.current }
  ) {
    override fun extract(color: Color) = color.alpha
    override fun apply(color: Color, value: Float) = color.copy(alpha = value)
  },
  RED(
    title = "R",
    color = { Color.Red }
  ) {
    override fun extract(color: Color) = color.red
    override fun apply(color: Color, value: Float) = color.copy(red = value)
  },
  GREEN(
    title = "G",
    color = { Color.Green }
  ) {
    override fun extract(color: Color) = color.green
    override fun apply(color: Color, value: Float) = color.copy(green = value)
  },
  BLUE(
    title = "B",
    color = { Color.Blue }
  ) {
    override fun extract(color: Color) = color.blue
    override fun apply(color: Color, value: Float) = color.copy(blue = value)
  };

  abstract fun extract(color: Color): Float
  abstract fun apply(color: Color, value: Float): Color
}

private enum class ColorPickerTab(val titleRes: Int) {
  COLORS(R.string.es_colors_tab_title), EDITOR(R.string.es_custom_tab_title)
}
