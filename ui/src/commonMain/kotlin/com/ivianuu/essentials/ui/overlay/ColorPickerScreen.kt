/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.ui.overlay

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import com.ivianuu.essentials.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.util.*
import com.ivianuu.injekt.*

class ColorPickerScreen(
  val initialColor: Color,
  val colorPalettes: List<ColorPickerPalette> = ColorPickerPalette.entries,
  val title: String? = null,
  val allowCustomArgb: Boolean = true,
  val showAlphaSelector: Boolean = false,
) : DialogScreen<Color> {
  @Provide companion object {
    @Provide fun ui(
      navigator: Navigator,
      screen: ColorPickerScreen
    ) = Ui<ColorPickerScreen> {
      var currentColor by remember { mutableStateOf(screen.initialColor) }
      var currentScreen by remember { mutableStateOf(ColorPickerTab.COLORS) }
      val otherScreen = when (currentScreen) {
        ColorPickerTab.COLORS -> ColorPickerTab.EDITOR
        ColorPickerTab.EDITOR -> ColorPickerTab.COLORS
      }


      Dialog(
        title = screen.title?.let { { Text(it) } },
        content = {
          AnimatedContent(
            modifier = Modifier.height(300.dp)
              .padding(start = 24.dp, end = 24.dp),
            state = currentScreen,
            transitionSpec = { materialSharedAxisY() }
          ) { currentScreen ->
            when (currentScreen) {
              ColorPickerTab.COLORS -> {
                ColorGrid(
                  modifier = Modifier.fillMaxSize(),
                  currentColor = currentColor,
                  colors = screen.colorPalettes,
                  onColorSelected = { currentColor = it }
                )
              }
              ColorPickerTab.EDITOR -> {
                ColorEditor(
                  modifier = Modifier.fillMaxSize(),
                  color = currentColor,
                  onColorChanged = { currentColor = it },
                  showAlphaSelector = screen.showAlphaSelector
                )
              }
            }
          }
        },
        buttons = {
          if (screen.allowCustomArgb) {
            TextButton(
              onClick = { currentScreen = otherScreen },
              colors = ButtonDefaults.textButtonColors(
                contentColor = currentColor
              )
            ) {
              Text(
                when (currentScreen) {
                  ColorPickerTab.COLORS -> "Colors"
                  ColorPickerTab.EDITOR -> "Custom"
                }
              )
            }
          }

          TextButton(onClick = scopedAction { navigator.pop(screen) }) { Text("Cancel") }

          TextButton(
            onClick = scopedAction { navigator.pop(screen, currentColor) },
            colors = ButtonDefaults.textButtonColors(
              contentColor = currentColor
            )
          ) { Text("OK") }
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
      AnimatedStack(
        items = palettesStack,
        transitionSpec = { materialSharedAxisX() }
      ) { palette ->
        val items = remember {
          palette
            .safeAs<ColorPickerPalette>()
            ?.colors
            ?.fastMap { ColorGridItem.Color(it) }
            ?.let { listOf(ColorGridItem.Back) + it }
            ?: colors.fastMap { ColorGridItem.Color(it.front) }
        }

        BoxWithConstraints(
          modifier = Modifier.padding(all = 4.dp)
            .then(modifier)
        ) {
          LazyColumn {
            items.chunked(4).fastForEach { rowItems ->
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
        items.fastForEach { item ->
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
      data object Back : ColorGridItem
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

    @SuppressLint("UnusedBoxWithConstraintsScope")
    @Composable private fun BaseColorGridItem(
      onClick: () -> Unit,
      content: @Composable () -> Unit
    ) {
      BoxWithConstraints {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(maxWidth)
            .padding(all = 4.dp)
            .wrapContentSize(Alignment.Center)
            .clickable(
              onClick = onClick,
              interactionSource = remember { MutableInteractionSource() },
              indication = ripple(bounded = false)
            ),
          contentAlignment = Alignment.Center
        ) {
          content()
        }
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

        ColorComponent.entries
          .fastFilter { it != ColorComponent.ALPHA || showAlphaSelector }
          .fastForEach { component ->
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
        textStyle = MaterialTheme.typography.titleLarge
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
          style = MaterialTheme.typography.titleLarge
        )

        MaterialTheme(
          colorScheme = MaterialTheme.colorScheme.copy(
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
          style = MaterialTheme.typography.titleLarge
        )
      }
    }

    private enum class ColorComponent(val title: String, val color: @Composable () -> Color) {
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

    private enum class ColorPickerTab { COLORS, EDITOR }
  }
}

enum class ColorPickerPalette {
  RED {
    override val front: Color
      get() = Color(0xFFF44336)
    override val colors: List<Color>
      get() = listOf(
        Color(0xFFFFEBEE),
        Color(0xFFFFCDD2),
        Color(0xFFEF9A9A),
        Color(0xFFE57373),
        Color(0xFFEF5350),
        Color(0xFFF44336),
        Color(0xFFE53935),
        Color(0xFFD32F2F),
        Color(0xFFC62828),
        Color(0xFFB71C1C),
        Color(0xFFFF8A80),
        Color(0xFFFF5252),
        Color(0xFFFF1744),
        Color(0xFFD50000)
      )

  },
  PINK {
    override val front: Color
      get() = Color(0xFFE91E63)
    override val colors: List<Color>
      get() = listOf(
        Color(0xFFFCE4EC),
        Color(0xFFF8BBD0),
        Color(0xFFF48FB1),
        Color(0xFFF06292),
        Color(0xFFEC407A),
        Color(0xFFE91E63),
        Color(0xFFD81B60),
        Color(0xFFC2185B),
        Color(0xFFAD1457),
        Color(0xFF880E4F),
        Color(0xFFFF80AB),
        Color(0xFFFF4081),
        Color(0xFFF50057),
        Color(0xFFC51162)
      )

  },
  PURPLE {
    override val front: Color
      get() = Color(0xFF9C27B0)
    override val colors: List<Color>
      get() = listOf(
        Color(0xFFF3E5F5),
        Color(0xFFE1BEE7),
        Color(0xFFCE93D8),
        Color(0xFFBA68C8),
        Color(0xFFAB47BC),
        Color(0xFF9C27B0),
        Color(0xFF8E24AA),
        Color(0xFF7B1FA2),
        Color(0xFF6A1B9A),
        Color(0xFF4A148C),
        Color(0xFFEA80FC),
        Color(0xFFE040FB),
        Color(0xFFD500F9),
        Color(0xFFAA00FF)
      )

  },
  DEEP_PURPLE {
    override val front: Color
      get() = Color(0xFF673AB7)
    override val colors: List<Color>
      get() = listOf(
        Color(0xFFEDE7F6),
        Color(0xFFD1C4E9),
        Color(0xFFB39DDB),
        Color(0xFF9575CD),
        Color(0xFF7E57C2),
        Color(0xFF673AB7),
        Color(0xFF5E35B1),
        Color(0xFF512DA8),
        Color(0xFF4527A0),
        Color(0xFF311B92),
        Color(0xFFB388FF),
        Color(0xFF7C4DFF),
        Color(0xFF651FFF),
        Color(0xFF6200EA)
      )

  },
  INDIGO {
    override val front: Color
      get() = Color(0xFF3F51B5)
    override val colors: List<Color>
      get() = listOf(
        Color(0xFFE8EAF6),
        Color(0xFFC5CAE9),
        Color(0xFF9FA8DA),
        Color(0xFF7986CB),
        Color(0xFF5C6BC0),
        Color(0xFF3F51B5),
        Color(0xFF3949AB),
        Color(0xFF303F9F),
        Color(0xFF283593),
        Color(0xFF1A237E),
        Color(0xFF8C9EFF),
        Color(0xFF536DFE),
        Color(0xFF3D5AFE),
        Color(0xFF304FFE)
      )

  },
  BLUE {
    override val front: Color
      get() = Color(0xFF2196F3)
    override val colors: List<Color>
      get() = listOf(
        Color(0xFFE3F2FD),
        Color(0xFFBBDEFB),
        Color(0xFF90CAF9),
        Color(0xFF64B5F6),
        Color(0xFF42A5F5),
        Color(0xFF2196F3),
        Color(0xFF1E88E5),
        Color(0xFF1976D2),
        Color(0xFF1565C0),
        Color(0xFF0D47A1),
        Color(0xFF82B1FF),
        Color(0xFF448AFF),
        Color(0xFF2979FF),
        Color(0xFF2962FF)
      )

  },
  LIGHT_BLUE {
    override val front: Color
      get() = Color(0xFF03A9F4)
    override val colors: List<Color>
      get() = listOf(
        Color(0xFFE1F5FE),
        Color(0xFFB3E5FC),
        Color(0xFF81D4FA),
        Color(0xFF4FC3F7),
        Color(0xFF29B6F6),
        Color(0xFF03A9F4),
        Color(0xFF039BE5),
        Color(0xFF0288D1),
        Color(0xFF0277BD),
        Color(0xFF01579B),
        Color(0xFF80D8FF),
        Color(0xFF40C4FF),
        Color(0xFF00B0FF),
        Color(
          0xFF0091EA
        )
      )

  },
  CYAN {
    override val front: Color
      get() = Color(0xFF00BCD4)
    override val colors: List<Color>
      get() = listOf(
        Color(0xFFE0F7FA),
        Color(0xFFB2EBF2),
        Color(0xFF80DEEA),
        Color(0xFF4DD0E1),
        Color(0xFF26C6DA),
        Color(0xFF00BCD4),
        Color(0xFF00ACC1),
        Color(0xFF0097A7),
        Color(0xFF00838F),
        Color(0xFF006064),
        Color(0xFF84FFFF),
        Color(0xFF18FFFF),
        Color(0xFF00E5FF),
        Color(0xFF00B8D4)
      )

  },
  TEAL {
    override val front: Color
      get() = Color(0xFF009688)
    override val colors: List<Color>
      get() = listOf(
        Color(0xFFE0F2F1),
        Color(0xFFB2DFDB),
        Color(0xFF80CBC4),
        Color(0xFF4DB6AC),
        Color(0xFF26A69A),
        Color(0xFF009688),
        Color(0xFF00897B),
        Color(0xFF00796B),
        Color(0xFF00695C),
        Color(0xFF004D40),
        Color(0xFFA7FFEB),
        Color(0xFF64FFDA),
        Color(0xFF1DE9B6),
        Color(0xFF00BFA5)
      )

  },
  GREEN {
    override val front: Color
      get() = Color(0xFF4CAF50)
    override val colors: List<Color>
      get() = listOf(
        Color(0xFFE8F5E9),
        Color(0xFFC8E6C9),
        Color(0xFFA5D6A7),
        Color(0xFF81C784),
        Color(0xFF66BB6A),
        Color(0xFF4CAF50),
        Color(0xFF43A047),
        Color(0xFF388E3C),
        Color(0xFF2E7D32),
        Color(0xFF1B5E20),
        Color(0xFFB9F6CA),
        Color(0xFF69F0AE),
        Color(0xFF00E676),
        Color(0xFF00C853)
      )

  },
  LIGHT_GREEN {
    override val front: Color
      get() = Color(0xFF8BC34A)
    override val colors: List<Color>
      get() = listOf(
        Color(0xFFF1F8E9),
        Color(0xFFDCEDC8),
        Color(0xFFC5E1A5),
        Color(0xFFAED581),
        Color(0xFF9CCC65),
        Color(0xFF8BC34A),
        Color(0xFF7CB342),
        Color(0xFF689F38),
        Color(0xFF558B2F),
        Color(0xFF33691E),
        Color(0xFFCCFF90),
        Color(0xFFB2FF59),
        Color(0xFF76FF03),
        Color(0xFF64DD17)
      )

  },
  LIME {
    override val front: Color
      get() = Color(0xFFCDDC39)
    override val colors: List<Color>
      get() = listOf(
        Color(0xFFF9FBE7),
        Color(0xFFF0F4C3),
        Color(0xFFE6EE9C),
        Color(0xFFDCE775),
        Color(0xFFD4E157),
        Color(0xFFCDDC39),
        Color(0xFFC0CA33),
        Color(0xFFAFB42B),
        Color(0xFF9E9D24),
        Color(0xFF827717),
        Color(0xFFF4FF81),
        Color(0xFFEEFF41),
        Color(0xFFC6FF00),
        Color(0xFFAEEA00)
      )

  },
  YELLOW {
    override val front: Color
      get() = Color(0xFFFFEB3B)
    override val colors: List<Color>
      get() = listOf(
        Color(0xFFFFFDE7),
        Color(0xFFFFF9C4),
        Color(0xFFFFF59D),
        Color(0xFFFFF176),
        Color(0xFFFFEE58),
        Color(0xFFFFEB3B),
        Color(0xFFFDD835),
        Color(0xFFFBC02D),
        Color(0xFFF9A825),
        Color(0xFFF57F17),
        Color(0xFFFFFF8D),
        Color(0xFFFFFF00),
        Color(0xFFFFEA00),
        Color(0xFFFFD600)
      )

  },
  AMBER {
    override val front: Color
      get() = Color(0xFFFFC107)
    override val colors: List<Color>
      get() = listOf(
        Color(0xFFFFF8E1),
        Color(0xFFFFECB3),
        Color(0xFFFFE082),
        Color(0xFFFFD54F),
        Color(0xFFFFCA28),
        Color(0xFFFFC107),
        Color(0xFFFFB300),
        Color(0xFFFFA000),
        Color(0xFFFF8F00),
        Color(0xFFFF6F00),
        Color(0xFFFFE57F),
        Color(0xFFFFD740),
        Color(0xFFFFC400),
        Color(0xFFFFAB00)
      )

  },
  ORANGE {
    override val front: Color
      get() = Color(0xFFFF9800)
    override val colors: List<Color>
      get() = listOf(
        Color(0xFFFFF3E0),
        Color(0xFFFFE0B2),
        Color(0xFFFFCC80),
        Color(0xFFFFB74D),
        Color(0xFFFFA726),
        Color(0xFFFF9800),
        Color(0xFFFB8C00),
        Color(0xFFF57C00),
        Color(0xFFEF6C00),
        Color(0xFFE65100),
        Color(0xFFFFD180),
        Color(0xFFFFAB40),
        Color(0xFFFF9100),
        Color(0xFFFF6D00)
      )

  },
  DEEP_ORANGE {
    override val front: Color
      get() = Color(0xFFFF5722)
    override val colors: List<Color>
      get() = listOf(
        Color(0xFFFBE9E7),
        Color(0xFFFFCCBC),
        Color(0xFFFFAB91),
        Color(0xFFFF8A65),
        Color(0xFFFF7043),
        Color(0xFFFF5722),
        Color(0xFFF4511E),
        Color(0xFFE64A19),
        Color(0xFFD84315),
        Color(0xFFBF360C),
        Color(0xFFFF6E40),
        Color(0xFFFFAB40),
        Color(0xFFFF3D00),
        Color(0xFFDD2C00)
      )

  },
  BROWN {
    override val front: Color
      get() = Color(0xFF795548)
    override val colors: List<Color>
      get() = listOf(
        Color(0xFFEFEBE9),
        Color(0xFFD7CCC8),
        Color(0xFFBCAAA4),
        Color(0xFFA1887F),
        Color(0xFF8D6E63),
        Color(0xFF795548),
        Color(0xFF6D4C41),
        Color(0xFF5D4037),
        Color(0xFF4E342E),
        Color(0xFF3E2723)
      )

  },
  GREY {
    override val front: Color
      get() = Color(0xFF9E9E9E)
    override val colors: List<Color>
      get() = listOf(
        Color(0xFFFAFAFA),
        Color(0xFFF5F5F5),
        Color(0xFFEEEEEE),
        Color(0xFFE0E0E0),
        Color(0xFFBDBDBD),
        Color(0xFF9E9E9E),
        Color(0xFF757575),
        Color(0xFF616161),
        Color(0xFF424242),
        Color(0xFF212121)
      )

  },
  BLUE_GREY {
    override val front: Color
      get() = Color(0xFF607D8B)
    override val colors: List<Color>
      get() = listOf(
        Color(0xFFECEFF1),
        Color(0xFFCFD8DC),
        Color(0xFFB0BEC5),
        Color(0xFF90A4AE),
        Color(0xFF78909C),
        Color(0xFF607D8B),
        Color(0xFF546E7A),
        Color(0xFF455A64),
        Color(0xFF37474F),
        Color(0xFF263238)
      )

  },
  BLACK {
    override val front: Color
      get() = Color(0xFF000000)
    override val colors: List<Color>
      get() = listOf(Color(0xFF000000))

  },
  WHITE {
    override val front: Color
      get() = Color(0xFFFFFFFF)
    override val colors: List<Color>
      get() = listOf(Color(0xFFFFFFFF))
  };

  abstract val front: Color
  abstract val colors: List<Color>
}
