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

inline fun Context.isWindowBackgroundDark() =
    colorAttr(android.R.attr.windowBackground).isDark

inline fun ContextAware.isWindowBackgroundDark() =
    providedContext.isWindowBackgroundDark()

inline fun Context.primaryColor() = colorAttr(android.R.attr.colorPrimary)

inline fun ContextAware.primaryColor() =
    providedContext.colorAttr(android.R.attr.colorPrimary)

inline fun Context.primaryColorDark() = colorAttr(android.R.attr.colorPrimaryDark)

inline fun ContextAware.primaryColorDark() =
    providedContext.primaryColorDark()

inline fun Context.accentColor() = colorAttr(android.R.attr.colorAccent)

inline fun ContextAware.accentColor() =
    providedContext.accentColor()

inline fun Context.cardColor() = cardColor(isWindowBackgroundDark())

inline fun Context.cardColor(color: Int) = cardColor(color.isDark)

inline fun Context.cardColor(dark: Boolean) = color(
    if (dark) {
        R.color.cardview_dark_background
    } else {
        R.color.cardview_light_background
    }
)

inline fun ContextAware.cardColor() =
    providedContext.cardColor()

inline fun ContextAware.cardColor(color: Int) =
    providedContext.cardColor(color)

inline fun ContextAware.cardColor(dark: Boolean) =
    providedContext.cardColor(dark)

inline fun Context.rippleColor() = rippleColor(isWindowBackgroundDark())

inline fun Context.rippleColor(color: Int) = rippleColor(color.isDark)

inline fun Context.rippleColor(dark: Boolean) = color(
    if (dark) {
        R.color.ripple_material_dark
    } else {
        R.color.ripple_material_light
    }
)

inline fun ContextAware.rippleColor() =
    providedContext.rippleColor()

inline fun ContextAware.rippleColor(color: Int) =
    providedContext.rippleColor(color)

inline fun ContextAware.rippleColor(dark: Boolean) =
    providedContext.rippleColor(dark)

inline fun Context.primaryTextColor() = primaryTextColor(isWindowBackgroundDark())

inline fun Context.primaryTextColor(color: Int) = primaryTextColor(color.isDark)

inline fun Context.primaryTextColor(dark: Boolean) = color(
    if (dark) {
        R.color.primary_text_default_material_dark
    } else {
        R.color.primary_text_default_material_light
    }
)

inline fun ContextAware.primaryTextColor() =
    providedContext.primaryTextColor()

inline fun ContextAware.primaryTextColor(color: Int) =
    providedContext.primaryTextColor(color)

inline fun ContextAware.primaryTextColor(dark: Boolean) =
    providedContext.primaryTextColor(dark)

inline fun Context.primaryDisabledTextColor() = primaryDisabledTextColor(isWindowBackgroundDark())

inline fun Context.primaryDisabledTextColor(color: Int) = primaryDisabledTextColor(color.isDark)

inline fun Context.primaryDisabledTextColor(dark: Boolean) = color(
    if (dark) {
        R.color.primary_text_disabled_material_dark
    } else {
        R.color.primary_text_disabled_material_light
    }
)

inline fun ContextAware.primaryDisabledTextColor() =
    providedContext.primaryDisabledTextColor()

inline fun ContextAware.primaryDisabledTextColor(color: Int) =
    providedContext.primaryDisabledTextColor(color)

inline fun ContextAware.primaryDisabledTextColor(dark: Boolean) =
    providedContext.primaryDisabledTextColor(dark)

inline fun Context.secondaryTextColor() = secondaryTextColor(isWindowBackgroundDark())

inline fun Context.secondaryTextColor(color: Int) = secondaryTextColor(color.isDark)

inline fun Context.secondaryTextColor(dark: Boolean) = color(
    if (dark) {
        R.color.secondary_text_default_material_dark
    } else {
        R.color.secondary_text_default_material_light
    }
)

inline fun ContextAware.secondaryTextColor() =
    providedContext.secondaryTextColor()

inline fun ContextAware.secondaryTextColor(color: Int) =
    providedContext.secondaryTextColor(color)

inline fun ContextAware.secondaryTextColor(dark: Boolean) =
    providedContext.secondaryTextColor(dark)

inline fun Context.secondaryDisabledTextColor() =
    secondaryDisabledTextColor(isWindowBackgroundDark())

inline fun Context.secondaryDisabledTextColor(color: Int) = secondaryDisabledTextColor(color.isDark)

inline fun Context.secondaryDisabledTextColor(dark: Boolean) = color(
    if (dark) {
        R.color.secondary_text_disabled_material_dark
    } else {
        R.color.secondary_text_disabled_material_light
    }
)

inline fun ContextAware.secondaryDisabledTextColor() =
    providedContext.secondaryDisabledTextColor()

inline fun ContextAware.secondaryDisabledTextColor(color: Int) =
    providedContext.secondaryDisabledTextColor(color)

inline fun ContextAware.secondaryDisabledTextColor(dark: Boolean) =
    providedContext.secondaryDisabledTextColor(dark)