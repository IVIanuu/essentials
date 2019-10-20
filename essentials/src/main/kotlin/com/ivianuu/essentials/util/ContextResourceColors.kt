/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.util

// todo remove

import android.annotation.SuppressLint
import android.content.Context
import com.ivianuu.essentials.R

fun Context.isWindowBackgroundDark(): Boolean =
    colorAttr(android.R.attr.windowBackground).isDark

fun Context.getPrimaryColor(): Int = colorAttr(R.attr.colorPrimary)

fun Context.getPrimaryColorDark(): Int = colorAttr(R.attr.colorPrimaryDark)

fun Context.getSecondaryColor(): Int = colorAttr(R.attr.colorSecondary)

fun Context.getCardColor(): Int = getCardColor(isWindowBackgroundDark())

fun Context.getCardColor(color: Int): Int = getCardColor(color.isDark)

fun Context.getCardColor(dark: Boolean): Int = color(
    if (dark) {
        R.color.cardview_dark_background
    } else {
        R.color.cardview_light_background
    }
)

fun Context.getIconColor(): Int = getIconColor(isWindowBackgroundDark())

fun Context.getIconColor(color: Int): Int = getIconColor(color.isDark)

fun Context.getIconColor(dark: Boolean): Int = color(
    if (dark) {
        R.color.primary_text_default_material_dark
    } else {
        R.color.secondary_text_default_material_light
    }
)

fun Context.getRippleColor(): Int = getRippleColor(isWindowBackgroundDark())

fun Context.getRippleColor(color: Int): Int = getRippleColor(color.isDark)

fun Context.getRippleColor(dark: Boolean): Int = color(
    if (dark) {
        R.color.ripple_material_dark
    } else {
        R.color.ripple_material_light
    }
)

fun Context.getPrimaryTextColor(): Int = getPrimaryTextColor(isWindowBackgroundDark())

fun Context.getPrimaryTextColor(color: Int): Int = getPrimaryTextColor(color.isDark)

fun Context.getPrimaryTextColor(dark: Boolean): Int = color(
    if (dark) {
        R.color.primary_text_default_material_dark
    } else {
        R.color.primary_text_default_material_light
    }
)

fun Context.getPrimaryDisabledTextColor(): Int =
    getPrimaryDisabledTextColor(isWindowBackgroundDark())

fun Context.getPrimaryDisabledTextColor(color: Int): Int = getPrimaryDisabledTextColor(color.isDark)

fun Context.getPrimaryDisabledTextColor(dark: Boolean): Int = color(
    if (dark) {
        R.color.primary_text_disabled_material_dark
    } else {
        R.color.primary_text_disabled_material_light
    }
)

fun Context.getSecondaryTextColor(): Int = getSecondaryTextColor(isWindowBackgroundDark())

fun Context.getSecondaryTextColor(color: Int): Int = getSecondaryTextColor(color.isDark)

fun Context.getSecondaryTextColor(dark: Boolean): Int = color(
    if (dark) {
        R.color.secondary_text_default_material_dark
    } else {
        R.color.secondary_text_default_material_light
    }
)

fun Context.getSecondaryDisabledTextColor(): Int =
    getSecondaryDisabledTextColor(isWindowBackgroundDark())

fun Context.getSecondaryDisabledTextColor(color: Int): Int =
    getSecondaryDisabledTextColor(color.isDark)

fun Context.getSecondaryDisabledTextColor(dark: Boolean): Int = color(
    if (dark) {
        R.color.secondary_text_disabled_material_dark
    } else {
        R.color.secondary_text_disabled_material_light
    }
)