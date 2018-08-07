package com.ivianuu.essentials.util.ext

import android.support.v7.graphics.Palette

fun Palette.getBestColor(fallbackColor: Int) = if (vibrantSwatch != null) {
    vibrantSwatch!!.rgb
} else if (mutedSwatch != null) {
    mutedSwatch!!.rgb
} else if (darkVibrantSwatch != null) {
    darkVibrantSwatch!!.rgb
} else if (darkMutedSwatch != null) {
    darkMutedSwatch!!.rgb
} else if (lightVibrantSwatch != null) {
    lightVibrantSwatch!!.rgb
} else if (lightMutedSwatch != null) {
    lightMutedSwatch!!.rgb
} else if (!swatches.isEmpty()) {
    swatches.maxBy { it.population }!!.rgb
} else {
    fallbackColor
}