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

@file:Suppress("NOTHING_TO_INLINE") // Aliases to other public API.

package com.ivianuu.essentials.util.ext

import android.annotation.TargetApi
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.DisplayMetrics
import android.view.Surface
import android.view.WindowManager

@PublishedApi
internal val systemMetrics: DisplayMetrics get() = Resources.getSystem().displayMetrics

@PublishedApi
internal val displayMetrics = DisplayMetrics()

inline val Int.dp: Float get() = (this * systemMetrics.density)

inline val Int.sp: Float get() = (this * systemMetrics.scaledDensity)

inline val Context.isPortrait: Boolean
    get() = !isLandscape

inline val Context.isLandscape: Boolean
    get() {
        val rotation = rotation
        return rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270
    }

inline val Context.rotation: Int
    get() = systemService<WindowManager>().defaultDisplay.rotation

inline val Context.screenHeight: Int
    get() {
        systemService<WindowManager>().defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

inline val Context.screenWidth: Int
    get() {
        systemService<WindowManager>().defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

inline val Context.realScreenHeight: Int
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    get() {
        systemService<WindowManager>().defaultDisplay.getRealMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

inline val Context.realScreenWidth: Int
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    get() {
        systemService<WindowManager>().defaultDisplay.getRealMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }