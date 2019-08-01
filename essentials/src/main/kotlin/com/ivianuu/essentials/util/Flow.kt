package com.ivianuu.essentials.util

import kotlinx.coroutines.flow.flow

fun <T> flowOf(block: suspend () -> T) = flow { emit(block()) }