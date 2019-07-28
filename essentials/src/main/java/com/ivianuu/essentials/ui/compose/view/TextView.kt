package com.ivianuu.essentials.ui.compose.view

import android.widget.TextView
import androidx.compose.ViewComposition
import com.ivianuu.essentials.ui.compose.sourceLocation

inline fun ViewComposition.TextView(noinline block: ViewDsl<TextView>.() -> Unit) =
    TextView(sourceLocation(), block)

fun ViewComposition.TextView(key: Any, block: ViewDsl<TextView>.() -> Unit) =
    View(key, { TextView(it) }, block)

fun <T : TextView> ViewDsl<T>.text(text: String?) {
    set(text) { this.text = it }
}

// todo replace with Compose text style
fun <T : TextView> ViewDsl<T>.textAppearance(textAppearance: Int) {
    set(textAppearance) { setTextAppearance(textAppearance) }
}