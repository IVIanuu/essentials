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

import android.content.Context
import com.ivianuu.essentials.R

val Context.isWindowBackgroundDark
    get() = resolveColorAttr(android.R.attr.windowBackground).isDark

fun Context.getPrimaryColor() = resolveColorAttr(android.R.attr.colorPrimary)

fun Context.getPrimaryColorDark() = resolveColorAttr(android.R.attr.colorPrimaryDark)

fun Context.getAccentColor() = resolveColorAttr(android.R.attr.colorAccent)

fun Context.getCardColor(isDark: Boolean = isWindowBackgroundDark): Int {
    return getResColor(
        if (isDark) {
            R.color.cardview_dark_background
        } else {
            R.color.cardview_light_background
        }
    )
}

fun Context.getRippleColor(isDark: Boolean = isWindowBackgroundDark): Int {
    return getResColor(
        if (isDark) {
            R.color.ripple_material_dark
        } else {
            R.color.ripple_material_light
        }
    )
}

fun Context.getPrimaryTextColor(isDark: Boolean = isWindowBackgroundDark): Int {
    return getResColor(
        if (isDark) {
            R.color.primary_text_default_material_dark
        } else {
            R.color.primary_text_default_material_light
        }
    )
}

fun Context.getPrimaryDisabledTextColor(isDark: Boolean = isWindowBackgroundDark): Int {
    return getResColor(
        if (isDark) {
            R.color.primary_text_disabled_material_dark
        } else {
            R.color.primary_text_disabled_material_light
        }
    )
}

fun Context.getSecondaryTextColor(isDark: Boolean = isWindowBackgroundDark): Int {
    return getResColor(
        if (isDark) {
            R.color.secondary_text_default_material_dark
        } else {
            R.color.secondary_text_default_material_light
        }
    )
}

fun Context.getSecondaryDisabledTextColor(isDark: Boolean = isWindowBackgroundDark): Int {
    return getResColor(
        if (isDark) {
            R.color.secondary_text_disabled_material_dark
        } else {
            R.color.secondary_text_disabled_material_light
        }
    )
}