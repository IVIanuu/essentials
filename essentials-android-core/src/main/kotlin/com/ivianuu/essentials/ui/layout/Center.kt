package com.ivianuu.essentials.ui.layout

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize

fun Modifier.center() = align(align = Alignment.Center)

fun Modifier.align(align: Alignment) = fillMaxSize()
    .wrapContentSize(align = align)
