package com.ivianuu.essentials.ui.compose.view

import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.ViewComposition
import androidx.ui.layout.Alignment
import com.ivianuu.essentials.ui.compose.sourceLocation

inline fun ViewComposition.TextView(noinline block: ViewDsl<AppCompatTextView>.() -> Unit) =
    TextView(sourceLocation(), block)

fun ViewComposition.TextView(key: Any, block: ViewDsl<AppCompatTextView>.() -> Unit) =
    View(key, { AppCompatTextView(it) }, block)

fun <T : TextView> ViewDsl<T>.text(text: String?) {
    set(text) { this.text = it }
}

// todo replace with Compose text style
fun <T : TextView> ViewDsl<T>.textAppearance(textAppearance: Int) {
    set(textAppearance) { setTextAppearance(textAppearance) }
}

fun <T : TextView> ViewDsl<T>.textGravity(gravity: Alignment) {
    set(gravity) { setGravity(gravity.toGravityInt()) }
}