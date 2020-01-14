package com.ivianuu.essentials.ui.core

import androidx.compose.Ambient
import androidx.compose.Composable
import androidx.compose.ambient

@Composable
val <T> Ambient<T>.current: T
    get() = ambient(this)

fun <T> ambientOf(defaultFactory: () -> T): Ambient<T> = Ambient.of(defaultFactory)
