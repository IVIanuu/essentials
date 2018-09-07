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
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.Surface
import android.view.View
import android.view.WindowManager
import com.ivianuu.essentials.util.ContextAware

@PublishedApi
internal val displayMetrics = DisplayMetrics()

inline fun Context.dpToPx(dp: Int) = dp * resources.displayMetrics.density

inline fun ContextAware.dpToPx(dp: Int) = providedContext.dpToPx(dp)

inline val Context.isPortrait: Boolean
    get() = !isLandscape

inline val ContextAware.isPortrait
    get() = providedContext.isPortrait

inline val Context.isLandscape: Boolean
    get() {
        val rotation = rotation
        return rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270
    }

inline val ContextAware.isLandscape
    get() = providedContext.isLandscape

inline val Context.rotation: Int
    get() = systemService<WindowManager>().defaultDisplay.rotation

inline val ContextAware.rotation
    get() = providedContext.rotation

inline val Context.screenWidth: Int
    get() {
        systemService<WindowManager>().defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

inline val ContextAware.screenWidth
    get() = providedContext.screenWidth

inline val Context.screenHeight: Int
    get() {
        systemService<WindowManager>().defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

inline val ContextAware.screenHeight
    get() = providedContext.screenHeight

inline val Context.realScreenWidth: Int
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    get() {
        systemService<WindowManager>().defaultDisplay.getRealMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }

inline val ContextAware.realScreenWidth
    get() = providedContext.realScreenWidth

inline val Context.realScreenHeight: Int
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    get() {
        systemService<WindowManager>().defaultDisplay.getRealMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

inline val ContextAware.realScreenHeight
    get() = providedContext.realScreenHeight

inline val Context.isRtl: Boolean
    get() = resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL

inline val ContextAware.isRtl get() = providedContext.isRtl

inline val Context.isLtr: Boolean
    get() = resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_LTR

inline val ContextAware.isLtr get() = providedContext.isLtr