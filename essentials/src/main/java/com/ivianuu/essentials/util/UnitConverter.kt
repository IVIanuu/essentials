package com.ivianuu.essentials.util

import android.content.res.Resources
import javax.inject.Inject

/**
 * Unit converter
 */
class UnitConverter @Inject constructor(private val resources: Resources) {

    fun dpToPx(dp: Int) = dp * resources.displayMetrics.density
}