package com.ivianuu.essentials.ui.compose.material

import android.widget.Button
import androidx.compose.ViewComposition
import com.ivianuu.essentials.ui.compose.sourceLocation
import com.ivianuu.essentials.ui.compose.view.View
import com.ivianuu.essentials.ui.compose.view.ViewDsl

// todo

inline fun ViewComposition.Button(noinline block: ViewDsl<Button>.() -> Unit) =
    Button(sourceLocation(), block)

fun ViewComposition.Button(key: Any, block: ViewDsl<Button>.() -> Unit) =
    View(key, { Button(it) }, block)