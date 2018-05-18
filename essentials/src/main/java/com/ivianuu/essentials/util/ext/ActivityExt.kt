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

package com.ivianuu.essentials.util.ext

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager

val Activity.contentView: View
    get() = findViewById(android.R.id.content)

fun Activity.hideInputMethod() {
    systemService<InputMethodManager>().hideSoftInputFromWindow(
        window.peekDecorView().windowToken,
        0
    )
}

fun Activity.showInputMethod(view: View) {
    systemService<InputMethodManager>().showSoftInput(view, 0)
}

fun Activity.finishWithoutTransition() {
    overridePendingTransition(0, 0)
    finish()
}

fun Activity.finishWithResult(resultCode: Int, data: Intent? = null) {
    setResult(resultCode, data)
    finish()
}

var Activity.statusBarColor: Int
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    get() = window.statusBarColor
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    set(value) {
        window.statusBarColor = value
    }

var Activity.statusBarColorCompat: Int
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        statusBarColor
    } else {
        -1
    }
    set(value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            statusBarColor = value
        }
    }

var Activity.isStatusBarLight: Boolean
    @TargetApi(Build.VERSION_CODES.M)
    get() = isSystemUiVisibilityFlagEnabled(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
    @TargetApi(Build.VERSION_CODES.M)
    set(value) {
        setSystemUiVisibilityFlag(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR, value)
    }

var Activity.isStatusBarLightCompat: Boolean
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

var Activity.isDrawUnderStatusBar: Boolean
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    get() = isSystemUiVisibilityFlagEnabled(View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    set(value) {
        setSystemUiVisibilityFlag(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN,
            value
        )
    }

var Activity.isDrawUnderStatusBarCompat: Boolean
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        isDrawUnderStatusBar
    } else {
        false
    }
    set(value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            isDrawUnderStatusBar = value
        }
    }

var Activity.isStatusBarTranslucent: Boolean
    @TargetApi(Build.VERSION_CODES.KITKAT)
    get() = hasWindowAttribute(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    @TargetApi(Build.VERSION_CODES.KITKAT)
    set(value) {
        setWindowAttribute(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, value)
    }

var Activity.isStatusBarTranslucentCompat: Boolean
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        isStatusBarTranslucent
    } else {
        false
    }
    set(value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            isStatusBarTranslucent = value
        }
    }

var Activity.isStatusBarTransparent: Boolean
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    get() = isDrawUnderStatusBar &&
            hasWindowAttribute(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            && statusBarColor == Color.TRANSPARENT
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    set(value) {
        setWindowAttribute(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, value)
        isDrawUnderStatusBar = value
        if (value) statusBarColor = Color.TRANSPARENT
    }

var Activity.isStatusBarTransparentCompat: Boolean
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        isStatusBarTransparent
    } else {
        false
    }
    set(value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            isStatusBarTransparent = value
        }
    }

var Activity.isStatusBarHidden: Boolean
    get() = hasWindowAttribute(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    set(value) {
        setWindowAttribute(WindowManager.LayoutParams.FLAG_FULLSCREEN, value)
    }

var Activity.navigationBarColor: Int
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    get() = window.navigationBarColor
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    set(value) {
        window.navigationBarColor = value
    }

var Activity.navigationBarColorCompat: Int
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        navigationBarColor
    } else {
        -1
    }
    set(value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            navigationBarColor = value
        }
    }

var Activity.isNavigationBarTranslucent: Boolean
    @TargetApi(Build.VERSION_CODES.KITKAT)
    get() = hasWindowAttribute(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
    @TargetApi(Build.VERSION_CODES.KITKAT)
    set(value) {
        setWindowAttribute(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, value)
    }

var Activity.isNavigationBarTranslucentCompat: Boolean
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        isNavigationBarTranslucent
    } else {
        false
    }
    set(value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            isNavigationBarTranslucent = value
        }
    }

var Activity.isNavigationBarTransparent: Boolean
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    get() = hasWindowAttribute(
        WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
                or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
    )
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    set(value) {
        setWindowAttribute(
            WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
                    or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, value
        )
    }

var Activity.isNavigationBarTransparentCompat: Boolean
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        isNavigationBarTransparent
    } else {
        false
    }
    set(value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            isNavigationBarTransparent = value
        }
    }

var Activity.isNavigationBarHidden: Boolean
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

var Activity.isNavigationBarHiddenCompat: Boolean
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        isNavigationBarHidden
    } else {
        false
    }
    set(value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            isNavigationBarHidden = value
        }
    }


var Activity.isNavigationBarLight: Boolean
    @TargetApi(Build.VERSION_CODES.O)
    get() = isSystemUiVisibilityFlagEnabled(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
    @TargetApi(Build.VERSION_CODES.O)
    set(value) {
        setSystemUiVisibilityFlag(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR, value)
    }

var Activity.isNavigationBarLightCompat: Boolean
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

private fun Activity.setSystemUiVisibilityFlag(flag: Int, enabled: Boolean) {
    val decorView = window.decorView
    val systemUiVisibility = decorView.systemUiVisibility
    if (enabled) {
        decorView.systemUiVisibility = systemUiVisibility or flag
    } else {
        decorView.systemUiVisibility = systemUiVisibility and flag.inv()
    }
}

private fun Activity.isSystemUiVisibilityFlagEnabled(flag: Int): Boolean {
    return window.decorView.systemUiVisibility and flag == flag
}

private fun Activity.setWindowAttribute(flag: Int, enabled: Boolean) {
    if (enabled) {
        window.addFlags(flag)
    } else {
        window.clearFlags(flag)
    }
}

private fun Activity.hasWindowAttribute(flag: Int): Boolean {
    return window.attributes.flags and flag == flag
}