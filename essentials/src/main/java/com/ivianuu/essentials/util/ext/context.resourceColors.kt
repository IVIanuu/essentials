@file:SuppressLint("PrivateResource")


// Aliases to other public API.

package com.ivianuu.essentials.util.ext

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.ivianuu.androidktx.core.content.color
import com.ivianuu.androidktx.core.content.colorAttr
import com.ivianuu.essentials.R
import com.ivianuu.essentials.util.ContextAware

fun Context.isWindowBackgroundDark() =
    colorAttr(android.R.attr.windowBackground).isDark

fun ContextAware.isWindowBackgroundDark() =
    providedContext.isWindowBackgroundDark()

fun View.isWindowBackgroundDark() = context.isWindowBackgroundDark()

fun Context.primaryColor() = colorAttr(android.R.attr.colorPrimary)

fun ContextAware.primaryColor() =
    providedContext.colorAttr(android.R.attr.colorPrimary)

fun View.primaryColor() =
    context.colorAttr(android.R.attr.colorPrimary)

fun Context.primaryColorDark() = colorAttr(android.R.attr.colorPrimaryDark)

fun ContextAware.primaryColorDark() =
    providedContext.primaryColorDark()

fun View.primaryColorDark() =
    context.primaryColorDark()

fun Context.accentColor() = colorAttr(android.R.attr.colorAccent)

fun ContextAware.accentColor() =
    providedContext.accentColor()

fun View.accentColor() =
    context.accentColor()

fun Context.cardColor() = cardColor(isWindowBackgroundDark())

fun Context.cardColor(color: Int) = cardColor(color.isDark)

fun Context.cardColor(dark: Boolean) = color(
    if (dark) {
        R.color.cardview_dark_background
    } else {
        R.color.cardview_light_background
    }
)

fun ContextAware.cardColor() =
    providedContext.cardColor()

fun ContextAware.cardColor(color: Int) =
    providedContext.cardColor(color)

fun ContextAware.cardColor(dark: Boolean) =
    providedContext.cardColor(dark)

fun View.cardColor() =
    context.cardColor()

fun View.cardColor(color: Int) =
    context.cardColor(color)

fun View.cardColor(dark: Boolean) =
    context.cardColor(dark)

fun Context.iconColor() = iconColor(isWindowBackgroundDark())

fun Context.iconColor(color: Int) = iconColor(color.isDark)

fun Context.iconColor(dark: Boolean) = color(
    if (dark) {
        R.color.primary_text_default_material_dark
    } else {
        R.color.secondary_text_default_material_light
    }
)

fun ContextAware.iconColor() =
    providedContext.iconColor()

fun ContextAware.iconColor(color: Int) =
    providedContext.iconColor(color)

fun ContextAware.iconColor(dark: Boolean) =
    providedContext.iconColor(dark)

fun View.iconColor() =
    context.iconColor()

fun View.iconColor(color: Int) =
    context.iconColor(color)

fun View.iconColor(dark: Boolean) =
    context.iconColor(dark)

fun Context.rippleColor() = rippleColor(isWindowBackgroundDark())

fun Context.rippleColor(color: Int) = rippleColor(color.isDark)

fun Context.rippleColor(dark: Boolean) = color(
    if (dark) {
        R.color.ripple_material_dark
    } else {
        R.color.ripple_material_light
    }
)

fun ContextAware.rippleColor() =
    providedContext.rippleColor()

fun ContextAware.rippleColor(color: Int) =
    providedContext.rippleColor(color)

fun ContextAware.rippleColor(dark: Boolean) =
    providedContext.rippleColor(dark)

fun View.rippleColor() =
    context.rippleColor()

fun View.rippleColor(color: Int) =
    context.rippleColor(color)

fun View.rippleColor(dark: Boolean) =
    context.rippleColor(dark)

fun Context.primaryTextColor() = primaryTextColor(isWindowBackgroundDark())

fun Context.primaryTextColor(color: Int) = primaryTextColor(color.isDark)

fun Context.primaryTextColor(dark: Boolean) = color(
    if (dark) {
        R.color.primary_text_default_material_dark
    } else {
        R.color.primary_text_default_material_light
    }
)

fun ContextAware.primaryTextColor() =
    providedContext.primaryTextColor()

fun ContextAware.primaryTextColor(color: Int) =
    providedContext.primaryTextColor(color)

fun ContextAware.primaryTextColor(dark: Boolean) =
    providedContext.primaryTextColor(dark)

fun View.primaryTextColor() =
    context.primaryTextColor()

fun View.primaryTextColor(color: Int) =
    context.primaryTextColor(color)

fun View.primaryTextColor(dark: Boolean) =
    context.primaryTextColor(dark)

fun Context.primaryDisabledTextColor() = primaryDisabledTextColor(isWindowBackgroundDark())

fun Context.primaryDisabledTextColor(color: Int) = primaryDisabledTextColor(color.isDark)

fun Context.primaryDisabledTextColor(dark: Boolean) = color(
    if (dark) {
        R.color.primary_text_disabled_material_dark
    } else {
        R.color.primary_text_disabled_material_light
    }
)

fun ContextAware.primaryDisabledTextColor() =
    providedContext.primaryDisabledTextColor()

fun ContextAware.primaryDisabledTextColor(color: Int) =
    providedContext.primaryDisabledTextColor(color)

fun ContextAware.primaryDisabledTextColor(dark: Boolean) =
    providedContext.primaryDisabledTextColor(dark)

fun View.primaryDisabledTextColor() =
    context.primaryDisabledTextColor()

fun View.primaryDisabledTextColor(color: Int) =
    context.primaryDisabledTextColor(color)

fun View.primaryDisabledTextColor(dark: Boolean) =
    context.primaryDisabledTextColor(dark)

fun Context.secondaryTextColor() = secondaryTextColor(isWindowBackgroundDark())

fun Context.secondaryTextColor(color: Int) = secondaryTextColor(color.isDark)

fun Context.secondaryTextColor(dark: Boolean) = color(
    if (dark) {
        R.color.secondary_text_default_material_dark
    } else {
        R.color.secondary_text_default_material_light
    }
)

fun ContextAware.secondaryTextColor() =
    providedContext.secondaryTextColor()

fun ContextAware.secondaryTextColor(color: Int) =
    providedContext.secondaryTextColor(color)

fun ContextAware.secondaryTextColor(dark: Boolean) =
    providedContext.secondaryTextColor(dark)

fun View.secondaryTextColor() =
    context.secondaryTextColor()

fun View.secondaryTextColor(color: Int) =
    context.secondaryTextColor(color)

fun View.secondaryTextColor(dark: Boolean) =
    context.secondaryTextColor(dark)

fun Context.secondaryDisabledTextColor() =
    secondaryDisabledTextColor(isWindowBackgroundDark())

fun Context.secondaryDisabledTextColor(color: Int) = secondaryDisabledTextColor(color.isDark)

fun Context.secondaryDisabledTextColor(dark: Boolean) = color(
    if (dark) {
        R.color.secondary_text_disabled_material_dark
    } else {
        R.color.secondary_text_disabled_material_light
    }
)

fun ContextAware.secondaryDisabledTextColor() =
    providedContext.secondaryDisabledTextColor()

fun ContextAware.secondaryDisabledTextColor(color: Int) =
    providedContext.secondaryDisabledTextColor(color)

fun ContextAware.secondaryDisabledTextColor(dark: Boolean) =
    providedContext.secondaryDisabledTextColor(dark)

fun View.secondaryDisabledTextColor() =
    context.secondaryDisabledTextColor()

fun View.secondaryDisabledTextColor(color: Int) =
    context.secondaryDisabledTextColor(color)

fun View.secondaryDisabledTextColor(dark: Boolean) =
    context.secondaryDisabledTextColor(dark)