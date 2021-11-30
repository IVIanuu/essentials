/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.colorpicker.ColorPickerPalette
import com.ivianuu.essentials.ui.animation.transition.CircularRevealStackTransition
import com.ivianuu.essentials.ui.animation.transition.CrossFadeStackTransition
import com.ivianuu.essentials.ui.animation.transition.FlipStackTransition
import com.ivianuu.essentials.ui.animation.transition.HorizontalStackTransition
import com.ivianuu.essentials.ui.animation.transition.SharedElement
import com.ivianuu.essentials.ui.animation.transition.SharedElementStackTransition
import com.ivianuu.essentials.ui.animation.transition.VerticalStackTransition
import com.ivianuu.essentials.ui.insets.LocalInsets
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiOptions
import com.ivianuu.essentials.ui.navigation.KeyUiOptionsFactory
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.launch

@Provide val transitionHomeItem = HomeItem("Transition") { TransitionKey.VERTICAL }

enum class TransitionKey(
  val title: String,
  val shared: Boolean,
  val color: Color,
  val fabAlignment: Alignment
) : Key<Unit> {
  VERTICAL(
    "Vertical Slide Animation",
    false,
    ColorPickerPalette.BLUE_GREY.colors[3],
    Alignment.BottomEnd
  ),
  CIRCULAR(
    "Circular Reveal Animation",
    false,
    ColorPickerPalette.RED.colors[3],
    Alignment.BottomEnd
  ),
  FADE(
    "Fade Animation",
    false,
    ColorPickerPalette.BLUE.colors[3],
    Alignment.BottomEnd
  ),
  FLIP(
    "Flip Animation",
    false,
    ColorPickerPalette.ORANGE.colors[3],
    Alignment.BottomEnd
  ),
  HORIZONTAL(
    "Horizontal Slide Animation",
    false,
    ColorPickerPalette.GREEN.colors[3],
    Alignment.BottomEnd
  ),
  ARC_FADE(
    "Fade Shared Element Transition",
    true,
    ColorPickerPalette.YELLOW.colors[3],
    Alignment.Center
  ),
  ARC_FADE_RESET(
    "Fade Shared Element Transition",
    true,
    ColorPickerPalette.PINK.colors[3],
    Alignment.BottomEnd
  )
}

@Provide fun transitionUi(
  key: TransitionKey,
  navigator: Navigator
) = KeyUi<TransitionKey> {
  Scaffold(
    topBar = {
      SharedElement(key = "app bar", isStart = false) {
        Box(
          modifier = Modifier.background(MaterialTheme.colors.primary)
            .fillMaxWidth()
            .height(56.dp)
        )
        TopAppBar(
          title = { Text("Transition") },
          leading = null
        )
      }
    }
  ) {
    Box(
      modifier = Modifier.fillMaxSize()
        .background(key.color)
    ) {
      Text(
        text = key.title,
        modifier = Modifier.align(Alignment.TopCenter)
          .padding(top = 72.dp),
        style = MaterialTheme.typography.h6
      )

      SharedElement(
        key = "fab",
        isStart = true,
        modifier = Modifier
          .align(key.fabAlignment)
          .padding(16.dp)
          .padding(bottom = LocalInsets.current.bottom)
      ) {
        val scope = rememberCoroutineScope()
        FloatingActionButton(
          onClick = {
            val next = TransitionKey.values()
              .getOrNull(TransitionKey.values().indexOf(key) + 1)
            if (next != null) scope.launch {
              navigator.push(next)
            }
          }
        ) {
          Icon(
            imageVector = Icons.Default.ArrowBack,
            modifier = Modifier.rotate(180f)
          )
        }
      }
    }
  }
}

@Provide val transitionUiOptionsFactory = KeyUiOptionsFactory<TransitionKey> { key ->
  val (enterTransition, exitTransition) = when (key) {
    TransitionKey.VERTICAL -> VerticalStackTransition() to VerticalStackTransition()
    TransitionKey.CIRCULAR -> CircularRevealStackTransition("fab") to
        CircularRevealStackTransition("fab")
    TransitionKey.FADE -> SharedElementStackTransition(
      "app bar" to "app bar",
      contentTransition = CrossFadeStackTransition()
    ) to SharedElementStackTransition(
      "app bar" to "app bar",
      contentTransition = CrossFadeStackTransition()
    )
    TransitionKey.FLIP -> SharedElementStackTransition(
      "app bar" to "app bar",
      contentTransition = FlipStackTransition()
    ) to SharedElementStackTransition(
      "app bar" to "app bar",
      contentTransition = FlipStackTransition()
    )
    TransitionKey.HORIZONTAL -> SharedElementStackTransition(
      "app bar" to "app bar",
      contentTransition = HorizontalStackTransition()
    ) to SharedElementStackTransition(
      "app bar" to "app bar",
      contentTransition = HorizontalStackTransition()
    )
    TransitionKey.ARC_FADE -> SharedElementStackTransition(
      "app bar" to "app bar",
      "fab" to "fab"
    ) to SharedElementStackTransition(
      "app bar" to "app bar",
      "fab" to "fab"
    )
    TransitionKey.ARC_FADE_RESET -> SharedElementStackTransition(
      "app bar" to "app bar",
      "fab" to "fab"
    ) to SharedElementStackTransition(
      "app bar" to "app bar",
      "fab" to "fab"
    )
  }
  KeyUiOptions(enterTransition = enterTransition, exitTransition = exitTransition)
}

