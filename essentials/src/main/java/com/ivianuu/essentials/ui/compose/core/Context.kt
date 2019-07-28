package com.ivianuu.essentials.ui.compose.core

import android.content.Context
import androidx.compose.ambient
import androidx.compose.effectOf
import androidx.ui.core.ContextAmbient

fun <R> withContext(block: Context.() -> R) = effectOf<R> {
    block(+ambient(ContextAmbient))
}