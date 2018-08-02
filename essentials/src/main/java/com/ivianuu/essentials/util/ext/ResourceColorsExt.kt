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
@file:SuppressLint("PrivateResource")
@file:Suppress("NOTHING_TO_INLINE")

// Aliases to other public API.

package com.ivianuu.essentials.util.ext

import android.annotation.SuppressLint
import android.content.Context
import com.ivianuu.essentials.R
import com.ivianuu.essentials.util.ContextAware

inline val Context.isWindowBackgroundDark
    get() = resolveColorAttr(android.R.attr.windowBackground).isDark

inline val ContextAware.isWindowBackgroundDark
    get() = providedContext.isWindowBackgroundDark

inline fun Context.getPrimaryColor() = resolveColorAttr(android.R.attr.colorPrimary)

inline fun ContextAware.getPrimaryColor() =
    providedContext.resolveColorAttr(android.R.attr.colorPrimary)

inline fun Context.getPrimaryColorDark() = resolveColorAttr(android.R.attr.colorPrimaryDark)

inline fun ContextAware.getPrimaryColorDark() =
    providedContext.getPrimaryColorDark()

inline fun Context.getAccentColor() = resolveColorAttr(android.R.attr.colorAccent)

inline fun ContextAware.getAccentColor() =
    providedContext.getAccentColor()

inline fun Context.getCardColor(isDark: Boolean = isWindowBackgroundDark): Int {
    return getResColor(
        if (isDark) {
            R.color.cardview_dark_background
        } else {
            R.color.cardview_light_background
        }
    )
}

inline fun ContextAware.getCardColor(isDark: Boolean = isWindowBackgroundDark) =
    providedContext.getCardColor(isDark)

inline fun Context.getRippleColor(isDark: Boolean = isWindowBackgroundDark): Int {
    return getResColor(
        if (isDark) {
            R.color.ripple_material_dark
        } else {
            R.color.ripple_material_light
        }
    )
}

inline fun ContextAware.getRippleColor(isDark: Boolean = isWindowBackgroundDark) =
    providedContext.getRippleColor(isDark)

inline fun Context.getPrimaryTextColor(isDark: Boolean = isWindowBackgroundDark): Int {
    return getResColor(
        if (isDark) {
            R.color.primary_text_default_material_dark
        } else {
            R.color.primary_text_default_material_light
        }
    )
}

inline fun ContextAware.getPrimaryTextColor(isDark: Boolean = isWindowBackgroundDark) =
    providedContext.getPrimaryTextColor(isDark)

inline fun Context.getPrimaryDisabledTextColor(isDark: Boolean = isWindowBackgroundDark): Int {
    return getResColor(
        if (isDark) {
            R.color.primary_text_disabled_material_dark
        } else {
            R.color.primary_text_disabled_material_light
        }
    )
}

inline fun ContextAware.getPrimaryDisabledTextColor(isDark: Boolean = isWindowBackgroundDark) =
    providedContext.getPrimaryDisabledTextColor(isDark)

inline fun Context.getSecondaryTextColor(isDark: Boolean = isWindowBackgroundDark): Int {
    return getResColor(
        if (isDark) {
            R.color.secondary_text_default_material_dark
        } else {
            R.color.secondary_text_default_material_light
        }
    )
}

inline fun ContextAware.getSecondaryTextColor(isDark: Boolean = isWindowBackgroundDark) =
    providedContext.getSecondaryTextColor(isDark)

inline fun Context.getSecondaryDisabledTextColor(isDark: Boolean = isWindowBackgroundDark): Int {
    return getResColor(
        if (isDark) {
            R.color.secondary_text_disabled_material_dark
        } else {
            R.color.secondary_text_disabled_material_light
        }
    )
}

inline fun ContextAware.getSecondaryDisabledTextColor(isDark: Boolean = isWindowBackgroundDark) =
    providedContext.getSecondaryDisabledTextColor(isDark)