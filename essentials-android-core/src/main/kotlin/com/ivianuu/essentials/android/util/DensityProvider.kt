package com.ivianuu.essentials.android.util

import android.content.Context
import androidx.ui.unit.Density
import com.ivianuu.injekt.Factory

@Factory
class DensityProvider(@PublishedApi internal val context: Context) {
    inline fun <R> withDensity(block: Density.() -> R): R =
        with(Density(context), block)
}
