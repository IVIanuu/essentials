package com.ivianuu.essentials

fun lerp(start: Float, stop: Float, fraction: Float) =
    (start * (1f - fraction) + stop * fraction)
