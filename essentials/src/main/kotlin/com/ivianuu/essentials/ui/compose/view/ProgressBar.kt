package com.ivianuu.essentials.ui.compose.view

import android.content.res.ColorStateList
import android.widget.ProgressBar
import androidx.compose.ViewComposition
import androidx.ui.graphics.Color
import androidx.ui.material.themeColor
import com.ivianuu.essentials.ui.compose.sourceLocation

inline fun ViewComposition.ProgressBar(noinline block: ViewDsl<ProgressBar>.() -> Unit) =
    ProgressBar(sourceLocation(), block)

fun ViewComposition.ProgressBar(key: Any, block: ViewDsl<ProgressBar>.() -> Unit) =
    View(key, { ProgressBar(it) }) {
        wrapContent()
        progressColor(+themeColor { secondary })
        block()
    }

fun <T : ProgressBar> ViewDsl<T>.progressColor(color: Color) {
    set(color) {
        val secondaryColorStateList = ColorStateList.valueOf(color.toArgb())
        indeterminateTintList = secondaryColorStateList
        progressTintList = secondaryColorStateList
        progressBackgroundTintList =
            ColorStateList.valueOf(color.copy(alpha = BackgroundOpacity).toArgb())
    }
}

// The opacity applied to the primary color to create the background color
private const val BackgroundOpacity = 0.24f