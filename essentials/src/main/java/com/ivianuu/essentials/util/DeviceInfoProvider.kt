package com.ivianuu.essentials.util

import android.content.res.Configuration
import android.content.res.Resources
import javax.inject.Inject

/**
 * Provides information about the device
 */
class DeviceInfoProvider @Inject constructor(private val resources: Resources) {

    val isTablet: Boolean
        get() = resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE

    val hasNavigationBar: Boolean
        get() {
            val id = resources.getIdentifier("config_showNavigationBar", "bool", "android")
            return id > 0 && resources.getBoolean(id)
        }
}