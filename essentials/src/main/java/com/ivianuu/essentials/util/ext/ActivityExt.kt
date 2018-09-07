/*
 * Copyright 2018 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("NOTHING_TO_INLINE")

package com.ivianuu.essentials.util.ext

import android.annotation.TargetApi
import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager

inline fun Activity.hideInputMethod() {
    systemService<InputMethodManager>().hideSoftInputFromWindow(
        window.peekDecorView().windowToken,
        0
    )
}

inline fun Activity.showInputMethod(view: View, flags: Int = 0) {
    systemService<InputMethodManager>().showSoftInput(view, flags)
}

inline var Activity.statusBarColor: Int
    get() = window.statusBarColor
    set(value) {
        window.statusBarColor = value
    }

inline var Activity.isStatusBarLight: Boolean
    @TargetApi(Build.VERSION_CODES.M)
    get() = isSystemUiVisibilityFlagEnabled(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
    @TargetApi(Build.VERSION_CODES.M)
    set(value) {
        setSystemUiVisibilityFlag(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR, value)
    }

inline var Activity.isStatusBarLightCompat: Boolean
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

inline var Activity.isDrawUnderStatusBar: Boolean
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    get() = isSystemUiVisibilityFlagEnabled(View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    set(value) {
        setSystemUiVisibilityFlag(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN,
            value
        )
    }

inline var Activity.isStatusBarTranslucent: Boolean
    @TargetApi(Build.VERSION_CODES.KITKAT)
    get() = hasWindowAttribute(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    @TargetApi(Build.VERSION_CODES.KITKAT)
    set(value) {
        setWindowAttribute(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, value)
    }

inline var Activity.isStatusBarTransparent: Boolean
    get() = isDrawUnderStatusBar &&
            hasWindowAttribute(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            && statusBarColor == Color.TRANSPARENT
    set(value) {
        setWindowAttribute(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, value)
        isDrawUnderStatusBar = value
        if (value) statusBarColor = Color.TRANSPARENT
    }

inline var Activity.isStatusBarHidden: Boolean
    get() = hasWindowAttribute(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    set(value) {
        setWindowAttribute(WindowManager.LayoutParams.FLAG_FULLSCREEN, value)
    }

inline var Activity.navigationBarColor: Int
    get() = window.navigationBarColor
    set(value) {
        window.navigationBarColor = value
    }

inline var Activity.isNavigationBarTranslucent: Boolean
    @TargetApi(Build.VERSION_CODES.KITKAT)
    get() = hasWindowAttribute(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
    @TargetApi(Build.VERSION_CODES.KITKAT)
    set(value) {
        setWindowAttribute(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, value)
    }

inline var Activity.isNavigationBarTransparent: Boolean
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

inline var Activity.isNavigationBarHidden: Boolean
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

inline var Activity.isNavigationBarLight: Boolean
    @TargetApi(Build.VERSION_CODES.O)
    get() = isSystemUiVisibilityFlagEnabled(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
    @TargetApi(Build.VERSION_CODES.O)
    set(value) {
        setSystemUiVisibilityFlag(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR, value)
    }

inline var Activity.isNavigationBarLightCompat: Boolean
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
internal fun Activity.setSystemUiVisibilityFlag(flag: Int, enabled: Boolean) {
    val decorView = window.decorView
    val systemUiVisibility = decorView.systemUiVisibility
    if (enabled) {
        decorView.systemUiVisibility = systemUiVisibility or flag
    } else {
        decorView.systemUiVisibility = systemUiVisibility and flag.inv()
    }
}

@PublishedApi
internal fun Activity.isSystemUiVisibilityFlagEnabled(flag: Int) =
    window.decorView.systemUiVisibility and flag == flag

@PublishedApi
internal fun Activity.setWindowAttribute(flag: Int, enabled: Boolean) {
    if (enabled) {
        window.addFlags(flag)
    } else {
        window.clearFlags(flag)
    }
}

@PublishedApi
internal fun Activity.hasWindowAttribute(flag: Int) =
    window.attributes.flags and flag == flag