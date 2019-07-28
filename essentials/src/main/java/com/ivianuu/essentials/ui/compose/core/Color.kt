package com.ivianuu.essentials.ui.compose.core

import androidx.ui.graphics.Color
import com.ivianuu.essentials.util.isDark

private fun Color.whiteOrBlack(alpha: Float) =
        (if (toArgb().isDark) Color.White else Color.Black).copy(alpha = alpha)

val Color.primaryTextColor: Color get() = whiteOrBlack(0.87f)
val Color.secondaryTextColor: Color get() = whiteOrBlack(0.60f)
val Color.disabledTextColor: Color get() = whiteOrBlack(0.38f)

val Color.activeIconColor: Color get() = whiteOrBlack(0.87f)
val Color.inactiveIconColor: Color get() = whiteOrBlack(0.60f)
val Color.disabledIconColor: Color get() = whiteOrBlack(0.38f)