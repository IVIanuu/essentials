package com.ivianuu.essentials.util

import android.content.Context
import androidx.ui.unit.Density
import com.ivianuu.injekt.ForApplication
import com.ivianuu.injekt.Unscoped

@Unscoped
class DensityProvider(@PublishedApi internal val context: @ForApplication Context) {
    inline fun <R> withDensity(block: Density.() -> R): R =
        with(Density(context), block)
}
