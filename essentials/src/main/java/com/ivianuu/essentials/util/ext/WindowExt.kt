package com.ivianuu.essentials.util.ext

import android.annotation.TargetApi
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager

inline var Window.isStatusBarLight: Boolean
    @TargetApi(Build.VERSION_CODES.M)
    get() = isSystemUiVisibilityFlagEnabled(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
    @TargetApi(Build.VERSION_CODES.M)
    set(value) {
        setSystemUiVisibilityFlag(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR, value)
    }

inline var Window.isStatusBarLightCompat: Boolean
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        isStatusBarLight
    } else {
        false
    }
    set(value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            isStatusBarLight = value
        }
    }

inline var Window.isDrawUnderStatusBar: Boolean
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    get() = isSystemUiVisibilityFlagEnabled(View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    set(value) {
        setSystemUiVisibilityFlag(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN,
            value
        )
    }

inline var Window.isStatusBarTranslucent: Boolean
    @TargetApi(Build.VERSION_CODES.KITKAT)
    get() = hasWindowAttribute(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    @TargetApi(Build.VERSION_CODES.KITKAT)
    set(value) {
        setWindowAttribute(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, value)
    }

inline var Window.isStatusBarTransparent: Boolean
    get() = isDrawUnderStatusBar &&
            hasWindowAttribute(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            && statusBarColor == Color.TRANSPARENT
    set(value) {
        setWindowAttribute(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, value)
        isDrawUnderStatusBar = value
        if (value) statusBarColor = Color.TRANSPARENT
    }

inline var Window.isStatusBarHidden: Boolean
    get() = hasWindowAttribute(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    set(value) {
        setWindowAttribute(WindowManager.LayoutParams.FLAG_FULLSCREEN, value)
    }

inline var Window.navigationBarColor: Int
    get() = navigationBarColor
    set(value) {
        navigationBarColor = value
    }

inline var Window.isNavigationBarTranslucent: Boolean
    @TargetApi(Build.VERSION_CODES.KITKAT)
    get() = hasWindowAttribute(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
    @TargetApi(Build.VERSION_CODES.KITKAT)
    set(value) {
        setWindowAttribute(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, value)
    }

inline var Window.isNavigationBarTransparent: Boolean
    get() = hasWindowAttribute(
        WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
    )
    set(value) {
        setWindowAttribute(
            WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, value
        )
    }

inline var Window.isNavigationBarHidden: Boolean
    @TargetApi(Build.VERSION_CODES.KITKAT)
    get() = hasWindowAttribute(
        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    )
    @TargetApi(Build.VERSION_CODES.KITKAT)
    set(value) {
        setWindowAttribute(
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY, value
        )
    }

inline var Window.isNavigationBarLight: Boolean
    @TargetApi(Build.VERSION_CODES.O)
    get() = isSystemUiVisibilityFlagEnabled(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
    @TargetApi(Build.VERSION_CODES.O)
    set(value) {
        setSystemUiVisibilityFlag(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR, value)
    }

inline var Window.isNavigationBarLightCompat: Boolean
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        isNavigationBarLight
    } else {
        false
    }
    set(value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            isNavigationBarLight = value
        }
    }

@PublishedApi
internal fun Window.setSystemUiVisibilityFlag(flag: Int, enabled: Boolean) {
    val decorView = decorView
    val systemUiVisibility = decorView.systemUiVisibility
    if (enabled) {
        decorView.systemUiVisibility = systemUiVisibility or flag
    } else {
        decorView.systemUiVisibility = systemUiVisibility and flag.inv()
    }
}

@PublishedApi
internal fun Window.isSystemUiVisibilityFlagEnabled(flag: Int) =
    decorView.systemUiVisibility and flag == flag

@PublishedApi
internal fun Window.setWindowAttribute(flag: Int, enabled: Boolean) {
    if (enabled) {
        addFlags(flag)
    } else {
        clearFlags(flag)
    }
}

@PublishedApi
internal fun Window.hasWindowAttribute(flag: Int) =
    attributes.flags and flag == flag