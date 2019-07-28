package com.ivianuu.essentials.ui.compose.view

import android.view.View
import android.widget.LinearLayout
import androidx.compose.ViewComposition
import androidx.ui.layout.Alignment
import com.ivianuu.essentials.ui.compose.sourceLocation

inline fun ViewComposition.LinearLayout(noinline block: ViewDsl<LinearLayout>.() -> Unit) =
    LinearLayout(sourceLocation(), block)

fun ViewComposition.LinearLayout(key: Any, block: ViewDsl<LinearLayout>.() -> Unit) =
    View(key, { LinearLayout(it) }, block)

fun <T : LinearLayout> ViewDsl<T>.orientation(orientation: Int) {
    set(orientation) { this.orientation = it }
}

fun <T : LinearLayout> ViewDsl<T>.gravity(gravity: Alignment) {
    set(gravity) { this.gravity = it.toGravityInt() }
}

fun <T : View> ViewDsl<T>.weight(weight: Float) {
    setLayoutParams(weight) {
        this as LinearLayout.LayoutParams
        this.weight = it
    }
}