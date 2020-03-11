package com.ivianuu.essentials.android.color

import androidx.ui.graphics.Color
import com.ivianuu.essentials.util.unsafeLazy

/**
 * Namespace for app colors
 *
 * Example usage:
 *
 * ´´´
 * val Colors.VividRed by lazyColor { Color(0xFFFFFFFF) }
 * ´´´
 *
 */
object Colors {
    object Essentials
}

fun lazyColor(init: () -> Color) = unsafeLazy(init)
