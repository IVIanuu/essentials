package com.ivianuu.essentials.ui.compose.view

import android.widget.ScrollView
import androidx.compose.ViewComposition
import com.ivianuu.essentials.ui.compose.sourceLocation

inline fun ViewComposition.ScrollView(noinline block: ViewDsl<ScrollView>.() -> Unit) =
    ScrollView(sourceLocation(), block)

fun ViewComposition.ScrollView(key: Any, block: ViewDsl<ScrollView>.() -> Unit) =
    View(key, { ScrollView(it) }, block)