package com.ivianuu.essentials.ui.dialog

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import com.ivianuu.essentials.ui.animation.ForFade
import com.ivianuu.essentials.ui.navigation.OverlayScreen
import com.ivianuu.essentials.ui.navigation.ScreenConfig
import com.ivianuu.injekt.Provide
import soup.compose.material.motion.MotionConstants.DefaultFadeOutDuration
import soup.compose.material.motion.animation.materialFadeIn
import soup.compose.material.motion.animation.materialFadeOut

interface DialogScreen<T> : OverlayScreen<T> {
  @Provide companion object {
    @Provide fun <T : DialogScreen<*>> config() = ScreenConfig<T>(opaque = true) {
      if (isPush) {
        DialogKey entersWith materialFadeIn()
        DialogScrimKey entersWith fadeIn(
          animationSpec = tween(
            durationMillis = 150.ForFade,
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
