/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.colorpicker

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.safeAs
import com.ivianuu.essentials.ui.animation.AnimatedContent
import com.ivianuu.essentials.ui.animation.AnimatedStack
import com.ivianuu.essentials.ui.common.CommonStrings
import com.ivianuu.essentials.ui.dialog.Dialog
import com.ivianuu.essentials.ui.layout.SquareFit
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.layout.squared
import com.ivianuu.essentials.ui.material.Slider
import com.ivianuu.essentials.ui.material.TextButton
import com.ivianuu.essentials.ui.material.guessingContentColorFor
import com.ivianuu.essentials.ui.util.toColorOrNull
import com.ivianuu.essentials.ui.util.toHexString
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide

@Composable fun ColorPickerDialog(
  modifier: Modifier = Modifier,
  initialColor: Color,
  colorPalettes: List<ColorPickerPalette> = ColorPickerPalette.values().toList(),
  icon: (@Composable () -> Unit)? = null,
  title: (@Composable () -> Unit)? = null,
  onColorSelected: (Color) -> Unit,
  onCancel: () -> Unit,
  allowCustomArgb: Boolean = true,
  showAlphaSelector: Boolean = false,
  @Inject colorPickerStrings: ColorPickerStrings,
  @Inject commonStrings: CommonStrings
) {
  var currentColor by remember { mutableStateOf(initialColor) }
  var currentScreen by remember { mutableStateOf(ColorPickerTab.COLORS) }
  val otherScreen = when (currentScreen) {
    ColorPickerTab.COLORS -> ColorPickerTab.EDITOR
    ColorPickerTab.EDITOR -> ColorPickerTab.COLORS
  }

  if (!allowCustomArgb && currentScreen == ColorPickerTab.EDITOR) {
    currentScreen = ColorPickerTab.COLORS
  }

  Dialog(
    modifier = modifier,
    applyContentPadding = false,
    icon = icon,
    title = title,
    content = {
      AnimatedContent(
        modifier = Modifier.height(300.dp)
          .padding(start = 24.dp, end = 24.dp),
        state = currentScreen
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
          Text(otherScreen.title(colorPickerStrings))
        }
      }

      TextButton(onClick = onCancel) { Text(commonStrings.cancel) }

      TextButton(
        onClick = { onColorSelected(currentColor) },
        colors = ButtonDefaults.textButtonColors(
          contentColor = currentColor
        )
      ) {
        Text(commonStrings.ok)
      }
    }
  )
}

private val NoPalette = Any()

@Composable private fun ColorGrid(
  currentColor: Color,
  colors: List<ColorPickerPalette>,
  onColorSelected: (Color) -> Unit,
  modifier: Modifier = Modifier
) {
  var palettesStack by remember { mutableStateOf(listOf(NoPalette)) }
  AnimatedStack(items = palettesStack) { palette ->
    val items = remember {
      palette
        .safeAs<ColorPickerPalette>()
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
                  ColorGridItem.Back -> palettesStack = listOf(NoPalette)
                  is ColorGridItem.Color -> {
                    if (palette !is ColorPickerPalette) {
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

private sealed interface ColorGridItem {
  object Back : ColorGridItem
  data class Color(val color: androidx.compose.ui.graphics.Color) : ColorGridItem
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
            color = LocalContentColor.current
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

private enum class ColorPickerTab(val title: ColorPickerStrings.() -> String) {
  COLORS({ ColorsTabTitle }), EDITOR({ EditorTabTitle })
}

interface ColorPickerStrings {
  val ColorPickerTitle: String
  val ColorsTabTitle: String
  val EditorTabTitle: String

  @Provide object Impl : ColorPickerStrings {
    override val ColorPickerTitle = "Pick a color"
    override val ColorsTabTitle = "Colors"
    override val EditorTabTitle = "Custom"
  }
}
