package com.ivianuu.essentials.util

import android.content.Context
import androidx.ui.unit.Density
import androidx.ui.unit.DensityScope
import androidx.ui.unit.withDensity
import com.ivianuu.injekt.Factory

@Factory
class DensityProvider(@PublishedApi internal val context: Context) {
    inline fun <R> withDensity(block: DensityScope.() -> R): R =
        withDensity(Density(context), block)
}
