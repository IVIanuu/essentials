package com.ivianuu.essentials.ui.dialog

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import com.ivianuu.essentials.ui.animation.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import soup.compose.material.motion.MotionConstants.DefaultFadeOutDuration
import soup.compose.material.motion.animation.*

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
