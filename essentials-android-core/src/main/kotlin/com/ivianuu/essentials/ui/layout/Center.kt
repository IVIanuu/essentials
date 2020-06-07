package com.ivianuu.essentials.ui.layout

import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.wrapContentSize

fun Modifier.center() = align(align = Alignment.Center)

fun Modifier.align(align: Alignment) = fillMaxSize()
    .wrapContentSize(align = align)
