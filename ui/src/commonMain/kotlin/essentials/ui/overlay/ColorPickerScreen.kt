/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.ui.overlay

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import essentials.compose.*
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import essentials.ui.util.*
import essentials.ui.util.toHexString
import injekt.*

class ColorPickerScreen(
  val initialColor: Color,
  val colorPalette: List<Color> = DefaultColorPalette,
  val includeAlpha: Boolean = false,
) : OverlayScreen<Color>

@Provide @Composable fun ColorPickerUi(
  navigator: Navigator,
  screen: ColorPickerScreen
): Ui<ColorPickerScreen> {
  var currentHex by remember {
    mutableStateOf(screen.initialColor.toHexString(includeAlpha = screen.includeAlpha))
  }
  val currentColor by remember {
    derivedStateOf { currentHex.toColorOrNull() ?: Color.Transparent }
  }

  EsModalBottomSheet(onDismissRequest = action { navigator.pop(screen, currentColor) }) {
    val textFieldContentColor = if (currentColor == Color.Transparent) LocalContentColor.current
    else guessingContentColorFor(currentColor)

    val textInputShape = MaterialTheme.shapes.small
    TextField(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .border(1.dp, LocalContentColor.current, textInputShape),
      prefix = { Text("#") },
      value = currentHex,
      onValueChange = { newValue ->
        if ((screen.includeAlpha && newValue.length > 8) ||
          (!screen.includeAlpha && newValue.length > 6)
        ) return@TextField

        currentHex = newValue
      },
      colors = TextFieldDefaults.colors(
        focusedContainerColor = currentColor,
        unfocusedContainerColor = currentColor,
        focusedTextColor = textFieldContentColor,
        unfocusedTextColor = textFieldContentColor,
        focusedPrefixColor = textFieldContentColor,
        unfocusedPrefixColor = textFieldContentColor,
        unfocusedIndicatorColor = Color.Transparent,
        focusedIndicatorColor = Color.Transparent
      ),
      shape = textInputShape
    )

    Spacer(Modifier.height(8.dp))

    EsLazyRow(
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(screen.colorPalette) { color ->
        Box(
          modifier = Modifier
            .size(48.dp)
            .background(color, CircleShape)
            .border(1.dp, LocalContentColor.current, CircleShape)
            .clickable(
              interactionSource = remember { MutableInteractionSource() },
              indication = ripple(bounded = false)
            ) { currentHex = color.toHexString(screen.includeAlpha) },
          contentAlignment = Alignment.Center
        ) {
          androidx.compose.animation.AnimatedVisibility(
            visible = currentColor == color,
            enter = fadeIn(),
            exit = fadeOut()
          ) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = null,
              tint = guessingContentColorFor(color)
            )
          }
        }
      }
    }

    Spacer(Modifier.height(8.dp))

    if (screen.includeAlpha)
      ColorComponentItem(
        title = "A",
        color = LocalContentColor.current,
        value = currentColor.alpha,
        onValueChange = {
          currentHex = currentHex
            .toColor()
            .copy(alpha = it)
            .toHexString(screen.includeAlpha)
        }
      )

    ColorComponentItem(
      title = "R",
      color = Color.Red,
      value = currentColor.red,
      onValueChange = {
        currentHex = currentHex
          .toColor()
          .copy(red = it)
          .toHexString(screen.includeAlpha)
      }
    )

    ColorComponentItem(
      title = "G",
      color = Color.Green,
      value = currentColor.green,
      onValueChange = {
        currentHex = currentHex
          .toColor()
          .copy(green = it)
          .toHexString(screen.includeAlpha)
      }
    )

    ColorComponentItem(
      title = "B",
      color = Color.Blue,
      value = currentColor.blue,
      onValueChange = {
        currentHex = currentHex
          .toColor()
          .copy(blue = it)
          .toHexString(screen.includeAlpha)
      }
    )

    Spacer(Modifier.height(8.dp))
  }
}

@Composable private fun ColorComponentItem(
  title: String,
  color: Color,
  value: Float,
  onValueChange: (Float) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .height(40.dp)
      .padding(horizontal = 16.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.titleSmall
    )

    Spacer(Modifier.width(8.dp))

    EsSlider(
      modifier = Modifier.weight(1f),
      value = value,
      onValueChange = onValueChange,
      colors = SliderDefaults.colors(
        thumbColor = color,
        activeTrackColor = color,
        inactiveTrackColor = color
      )
    )

    Spacer(Modifier.width(8.dp))

    Text(
      text = (255 * value).toInt().toString(),
      style = MaterialTheme.typography.labelMedium,
      textAlign = TextAlign.End
    )
  }
}

val DefaultColorPalette = listOf(
  Color(0xFFF44336), // RED
  Color(0xFFE91E63), // PINK
  Color(0xFF9C27B0), // PURPLE
  Color(0xFF673AB7), // DEEP_PURPLE
  Color(0xFF3F51B5), // INDIGO
  Color(0xFF2196F3), // BLUE
  Color(0xFF03A9F4), // LIGHT_BLUE
  Color(0xFF00BCD4), // CYAN
  Color(0xFF009688), // TEAL
  Color(0xFF4CAF50), // GREEN
  Color(0xFF8BC34A), // LIGHT_GREEN
  Color(0xFFCDDC39), // LIME
  Color(0xFFFFEB3B), // YELLOW
  Color(0xFFFFC107), // AMBER
  Color(0xFFFF9800), // ORANGE
  Color(0xFFFF5722)  // DEEP_ORANGE
)
