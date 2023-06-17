package com.ivianuu.essentials.ui.dialog

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import com.ivianuu.essentials.ui.animation.DefaultFadeOutDuration
import com.ivianuu.essentials.ui.animation.forMaterialFade
import com.ivianuu.essentials.ui.animation.materialFadeIn
import com.ivianuu.essentials.ui.animation.materialFadeOut
import com.ivianuu.essentials.ui.navigation.OverlayScreen
import com.ivianuu.essentials.ui.navigation.ScreenConfig
import com.ivianuu.injekt.Provide

interface DialogScreen<T> : OverlayScreen<T> {
  companion object {
    @Provide fun <T : DialogScreen<*>> config() = ScreenConfig<T>(opaque = true) {
      if (isPush) {
        DialogKey entersWith materialFadeIn()
        DialogScrimKey entersWith fadeIn(
          animationSpec = tween(
            durationMillis = 150.forMaterialFade,
            easing = LinearEasing
          )
        )
      } else {
        DialogKey exitsWith materialFadeOut()
        DialogScrimKey exitsWith fadeOut(
          animationSpec = tween(
            durationMillis = DefaultFadeOutDuration,
            easing = LinearEasing
          )
        )
      }
    }
  }
}

val DialogKey = "dialog"
val DialogScrimKey = "dialog_scrim"
