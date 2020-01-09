package com.ivianuu.essentials.util

import android.content.Context
import androidx.ui.core.Density
import androidx.ui.core.DensityScope
import androidx.ui.core.withDensity
import com.ivianuu.injekt.Factory

@Factory
class DensityProvider(@PublishedApi internal val context: Context) {
    inline fun <R> withDensity(block: DensityScope.() -> R): R =
        withDensity(Density(context), block)
}