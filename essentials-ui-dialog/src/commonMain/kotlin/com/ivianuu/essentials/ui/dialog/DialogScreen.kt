package com.ivianuu.essentials.ui.dialog

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import com.ivianuu.essentials.ui.navigation.OverlayScreen
import com.ivianuu.essentials.ui.navigation.ScreenConfig
import com.ivianuu.injekt.Provide

interface DialogScreen<T> : OverlayScreen<T> {
  companion object {
    @Provide fun <T : DialogScreen<*>> config() = ScreenConfig<T>(opaque = true) {
      if (isPush) {
        DialogKey entersWith
            scaleIn(
              animationSpec = tween(150, easing = LinearOutSlowInEasing),
              initialScale = 0.8f
            ) + fadeIn(tween(50))
        DialogScrimKey entersWith fadeIn(tween(300))
      } else {
        DialogKey exitsWith fadeOut(tween(75))
        DialogScrimKey exitsWith fadeOut(tween(75))
      }
    }
  }
}

val DialogKey = Any()
val DialogScrimKey = Any()
