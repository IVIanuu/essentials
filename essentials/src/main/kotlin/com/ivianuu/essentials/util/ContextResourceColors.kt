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
import android.view.View
import com.ivianuu.essentials.R
import com.ivianuu.kommon.core.content.color
import com.ivianuu.kommon.core.content.colorAttr

fun Context.isWindowBackgroundDark(): Boolean =
    colorAttr(android.R.attr.windowBackground).isDark

fun ContextAware.isWindowBackgroundDark(): Boolean =
    providedContext.isWindowBackgroundDark()

fun View.isWindowBackgroundDark(): Boolean = context.isWindowBackgroundDark()

fun Context.getPrimaryColor(): Int = colorAttr(R.attr.colorPrimary)

fun ContextAware.getPrimaryColor(): Int =
    providedContext.getPrimaryColor()

fun View.getPrimaryColor(): Int =
    context.getPrimaryColor()

fun Context.getPrimaryColorDark(): Int = colorAttr(R.attr.colorPrimaryDark)

fun ContextAware.getPrimaryColorDark(): Int =
    providedContext.getPrimaryColorDark()

fun View.getPrimaryColorDark(): Int =
    context.getPrimaryColorDark()

fun Context.getSecondaryColor(): Int = colorAttr(R.attr.colorSecondary)

fun ContextAware.getSecondaryColor(): Int =
    providedContext.getSecondaryColor()

fun View.getSecondaryColor(): Int =
    context.getSecondaryColor()

fun Context.getCardColor(): Int = getCardColor(isWindowBackgroundDark())

fun Context.getCardColor(color: Int): Int = getCardColor(color.isDark)

fun Context.getCardColor(dark: Boolean): Int = color(
    if (dark) {
        R.color.cardview_dark_background
    } else {
        R.color.cardview_light_background
    }
)

fun ContextAware.getCardColor(): Int =
    providedContext.getCardColor()

fun ContextAware.getCardColor(color: Int): Int =
    providedContext.getCardColor(color)

fun ContextAware.getCardColor(dark: Boolean): Int =
    providedContext.getCardColor(dark)

fun View.getCardColor(): Int =
    context.getCardColor()

fun View.getCardColor(color: Int): Int =
    context.getCardColor(color)

fun View.getCardColor(dark: Boolean): Int =
    context.getCardColor(dark)

fun Context.getIconColor(): Int = getIconColor(isWindowBackgroundDark())

fun Context.getIconColor(color: Int): Int = getIconColor(color.isDark)

fun Context.getIconColor(dark: Boolean): Int = color(
    if (dark) {
        R.color.primary_text_default_material_dark
    } else {
        R.color.secondary_text_default_material_light
    }
)

fun ContextAware.getIconColor(): Int =
    providedContext.getIconColor()

fun ContextAware.getIconColor(color: Int): Int =
    providedContext.getIconColor(color)

fun ContextAware.getIconColor(dark: Boolean): Int =
    providedContext.getIconColor(dark)

fun View.getIconColor(): Int =
    context.getIconColor()

fun View.getIconColor(color: Int): Int =
    context.getIconColor(color)

fun View.getIconColor(dark: Boolean): Int =
    context.getIconColor(dark)

fun Context.getRippleColor(): Int = getRippleColor(isWindowBackgroundDark())

fun Context.getRippleColor(color: Int): Int = getRippleColor(color.isDark)

fun Context.getRippleColor(dark: Boolean): Int = color(
    if (dark) {
        R.color.ripple_material_dark
    } else {
        R.color.ripple_material_light
    }
)

fun ContextAware.getRippleColor(): Int = providedContext.getRippleColor()

fun ContextAware.getRippleColor(color: Int): Int =
    providedContext.getRippleColor(color)

fun ContextAware.getRippleColor(dark: Boolean): Int =
    providedContext.getRippleColor(dark)

fun View.getRippleColor(): Int =
    context.getRippleColor()

fun View.getRippleColor(color: Int): Int =
    context.getRippleColor(color)

fun View.getRippleColor(dark: Boolean): Int =
    context.getRippleColor(dark)

fun Context.getPrimaryTextColor(): Int = getPrimaryTextColor(isWindowBackgroundDark())

fun Context.getPrimaryTextColor(color: Int): Int = getPrimaryTextColor(color.isDark)

fun Context.getPrimaryTextColor(dark: Boolean): Int = color(
    if (dark) {
        R.color.primary_text_default_material_dark
    } else {
        R.color.primary_text_default_material_light
    }
)

fun ContextAware.getPrimaryTextColor(): Int =
    providedContext.getPrimaryTextColor()

fun ContextAware.getPrimaryTextColor(color: Int): Int =
    providedContext.getPrimaryTextColor(color)

fun ContextAware.getPrimaryTextColor(dark: Boolean): Int =
    providedContext.getPrimaryTextColor(dark)

fun View.getPrimaryTextColor(): Int =
    context.getPrimaryTextColor()

fun View.getPrimaryTextColor(color: Int): Int =
    context.getPrimaryTextColor(color)

fun View.getPrimaryTextColor(dark: Boolean): Int =
    context.getPrimaryTextColor(dark)

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

fun ContextAware.getPrimaryDisabledTextColor(): Int =
    providedContext.getPrimaryDisabledTextColor()

fun ContextAware.getPrimaryDisabledTextColor(color: Int): Int =
    providedContext.getPrimaryDisabledTextColor(color)

fun ContextAware.getPrimaryDisabledTextColor(dark: Boolean): Int =
    providedContext.getPrimaryDisabledTextColor(dark)

fun View.getPrimaryDisabledTextColor(): Int =
    context.getPrimaryDisabledTextColor()

fun View.getPrimaryDisabledTextColor(color: Int): Int =
    context.getPrimaryDisabledTextColor(color)

fun View.getPrimaryDisabledTextColor(dark: Boolean): Int =
    context.getPrimaryDisabledTextColor(dark)

fun Context.getSecondaryTextColor(): Int = getSecondaryTextColor(isWindowBackgroundDark())

fun Context.getSecondaryTextColor(color: Int): Int = getSecondaryTextColor(color.isDark)

fun Context.getSecondaryTextColor(dark: Boolean): Int = color(
    if (dark) {
        R.color.secondary_text_default_material_dark
    } else {
        R.color.secondary_text_default_material_light
    }
)

fun ContextAware.getSecondaryTextColor(): Int =
    providedContext.getSecondaryTextColor()

fun ContextAware.getSecondaryTextColor(color: Int): Int =
    providedContext.getSecondaryTextColor(color)

fun ContextAware.getSecondaryTextColor(dark: Boolean): Int =
    providedContext.getSecondaryTextColor(dark)

fun View.getSecondaryTextColor(): Int =
    context.getSecondaryTextColor()

fun View.getSecondaryTextColor(color: Int): Int =
    context.getSecondaryTextColor(color)

fun View.getSecondaryTextColor(dark: Boolean): Int =
    context.getSecondaryTextColor(dark)

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

fun ContextAware.getSecondaryDisabledTextColor(): Int =
    providedContext.getSecondaryDisabledTextColor()

fun ContextAware.getSecondaryDisabledTextColor(color: Int): Int =
    providedContext.getSecondaryDisabledTextColor(color)

fun ContextAware.getSecondaryDisabledTextColor(dark: Boolean): Int =
    providedContext.getSecondaryDisabledTextColor(dark)

fun View.getSecondaryDisabledTextColor(): Int =
    context.getSecondaryDisabledTextColor()

fun View.getSecondaryDisabledTextColor(color: Int): Int =
    context.getSecondaryDisabledTextColor(color)

fun View.getSecondaryDisabledTextColor(dark: Boolean): Int =
    context.getSecondaryDisabledTextColor(dark)