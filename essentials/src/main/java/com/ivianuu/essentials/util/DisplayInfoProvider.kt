package com.ivianuu.essentials.util

import android.util.DisplayMetrics
import android.view.Surface
import android.view.WindowManager
import javax.inject.Inject

/**
 * Provides information about the screen
 */
class DisplayInfoProvider @Inject constructor(private val windowManager: WindowManager) {

    private val displayMetrics = DisplayMetrics()

    val rotation get() = windowManager.defaultDisplay.rotation

    val isPortrait get() = !isLandscape

    val isLandscape
        get() =
            rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270

    val screenWidth: Int
        get() {
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.widthPixels
        }

    val screenHeight: Int
        get() {
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.heightPixels
        }

    val realScreenWidth: Int
        get() {
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
            return displayMetrics.widthPixels
        }

    val realScreenHeight: Int
        get() {
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
            return displayMetrics.heightPixels
        }
}