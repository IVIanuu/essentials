package com.ivianuu.essentials.ui.compose.view

import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.compose.ViewComposition
import androidx.ui.layout.Alignment
import com.ivianuu.essentials.ui.compose.sourceLocation

inline fun ViewComposition.FrameLayout(noinline block: ViewDsl<FrameLayout>.() -> Unit) =
    FrameLayout(
        sourceLocation(), block
    )

fun ViewComposition.FrameLayout(key: Any, block: ViewDsl<FrameLayout>.() -> Unit) =
    View(key, { FrameLayout(it) }, block)

// lp
fun <T : View> ViewDsl<T>.gravity(gravity: Alignment) {
    setLayoutParams(gravity) {
        when (this) {
            is FrameLayout.LayoutParams -> this.gravity = gravity.toGravityInt()
            is LinearLayout.LayoutParams -> this.gravity = gravity.toGravityInt()
            else -> error("Cannot set gravity on ${this.javaClass.name}")
        }
    }
}