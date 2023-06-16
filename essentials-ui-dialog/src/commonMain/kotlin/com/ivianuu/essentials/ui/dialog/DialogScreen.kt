package com.ivianuu.essentials.ui.dialog

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import com.ivianuu.essentials.ui.navigation.OverlayScreen
import com.ivianuu.essentials.ui.navigation.ScreenConfig
import com.ivianuu.injekt.Provide

interface DialogScreen<T> : OverlayScreen<T> {
  companion object {
    @Provide fun <T : DialogScreen<*>> config() = ScreenConfig<T>(opaque = true) {
      println("is push $isPush")
      if (isPush) {
        DialogKey entersWith
            scaleIn(tween(150, easing = LinearOutSlowInEasing), 0.8f) +
            fadeIn(tween(100, easing = LinearOutSlowInEasing))
        DialogScrimKey entersWith fadeIn(tween(100))
      } else {
        DialogKey exitsWith
            scaleOut(tween(100, easing = FastOutSlowInEasing), 0.8f) +
            fadeOut(tween(100, easing = FastOutSlowInEasing))
        DialogScrimKey exitsWith fadeOut(tween(500, easing = FastOutSlowInEasing))
      }
    }
  }
}

val DialogKey = "dialog"
val DialogScrimKey = "dialog_scrim"
