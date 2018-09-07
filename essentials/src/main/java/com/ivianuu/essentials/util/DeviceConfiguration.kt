package com.ivianuu.essentials.util

import android.content.res.Resources
import android.view.View
import javax.inject.Inject

/**
 * Device configuration
 */
class DeviceConfiguration @Inject constructor(private val resources: Resources) {

    val isRtl: Boolean
        get() = resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL

    val isLtr: Boolean
        get() = resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_LTR
}