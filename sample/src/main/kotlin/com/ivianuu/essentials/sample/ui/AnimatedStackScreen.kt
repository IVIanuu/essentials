package com.ivianuu.essentials.sample.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ivianuu.essentials.colorpicker.ColorPickerPalette
import com.ivianuu.essentials.ui.animation.three.AnimatedStack
import com.ivianuu.essentials.ui.animation.three.AnimatedStackScope
import com.ivianuu.essentials.ui.backpress.BackHandler
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.injekt.Provide

@Provide val animatedStackHomeItem = HomeItem("Animated Stack") { AnimatedStackScreen() }

@Provide class AnimatedStackScreen : Screen<Unit>

@OptIn(ExperimentalAnimationApi::class)
@Provide val animatedStackUi = Ui<AnimatedStackScreen, Unit> {
  var backStack by remember { mutableStateOf(listOf(Color.White)) }

  AnimatedStack(
    backStack,
    transitionSpec = {
      when (initialState.size) {
        targetState.size -> fadeIn() with fadeOut()
        else -> {
          val isPush = initialState.size < targetState.size
          val direction = if (isPush) AnimatedStackScope.SlideDirection.Left
          else AnimatedStackScope.SlideDirection.Right
          slideIntoContainer(direction, tween(2000)) with
              slideOutOfContainer(direction, tween(2000))
        }
      }
    }
  ) { color ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(color)
        .clickable {
          backStack += ColorPickerPalette.values()
            .toList()
            .shuffled()
            .first()
            .colors
            .shuffled()
            .first()
        }
    )

    BackHandler(enabled = backStack.size > 1) {
      backStack -= color
    }
  }
}
