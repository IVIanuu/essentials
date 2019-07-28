package com.ivianuu.essentials.ui.compose.view

import android.widget.FrameLayout
import androidx.compose.ViewComposition
import com.ivianuu.essentials.ui.compose.sourceLocation

inline fun ViewComposition.FrameLayout(noinline block: ViewDsl<FrameLayout>.() -> Unit) =
    FrameLayout(
        sourceLocation(), block
    )

fun ViewComposition.FrameLayout(key: Any, block: ViewDsl<FrameLayout>.() -> Unit) =
    View(key, { FrameLayout(it) }, block)