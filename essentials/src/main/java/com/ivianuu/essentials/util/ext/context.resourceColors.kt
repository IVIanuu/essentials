@file:SuppressLint("PrivateResource")


// Aliases to other public API.

package com.ivianuu.essentials.util.ext

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.ivianuu.essentials.R
import com.ivianuu.essentials.util.ContextAware
import com.ivianuu.kommon.core.content.color
import com.ivianuu.kommon.core.content.colorAttr

fun Context.isWindowBackgroundDark(): Boolean =
    colorAttr(android.R.attr.windowBackground).isDark

fun ContextAware.isWindowBackgroundDark(): Boolean =
    providedContext.isWindowBackgroundDark()

fun View.isWindowBackgroundDark(): Boolean = context.isWindowBackgroundDark()

fun Context.primaryColor(): Int = colorAttr(android.R.attr.colorPrimary)

fun ContextAware.primaryColor(): Int =
    providedContext.colorAttr(android.R.attr.colorPrimary)

fun View.primaryColor(): Int =
    context.colorAttr(android.R.attr.colorPrimary)

fun Context.primaryColorDark(): Int = colorAttr(android.R.attr.colorPrimaryDark)

fun ContextAware.primaryColorDark(): Int =
    providedContext.primaryColorDark()

fun View.primaryColorDark(): Int =
    context.primaryColorDark()

fun Context.secondaryColor(): Int = colorAttr(android.R.attr.colorSecondary)

fun ContextAware.secondaryColor(): Int =
    providedContext.secondaryColor()

fun View.secondaryColor(): Int =
    context.secondaryColor()

fun Context.cardColor(): Int = cardColor(isWindowBackgroundDark())

fun Context.cardColor(color: Int): Int = cardColor(color.isDark)

fun Context.cardColor(dark: Boolean): Int = color(
    if (dark) {
        R.color.cardview_dark_background
    } else {
        R.color.cardview_light_background
    }
)

fun ContextAware.cardColor(): Int =
    providedContext.cardColor()

fun ContextAware.cardColor(color: Int): Int =
    providedContext.cardColor(color)

fun ContextAware.cardColor(dark: Boolean): Int =
    providedContext.cardColor(dark)

fun View.cardColor(): Int =
    context.cardColor()

fun View.cardColor(color: Int): Int =
    context.cardColor(color)

fun View.cardColor(dark: Boolean): Int =
    context.cardColor(dark)

fun Context.iconColor(): Int = iconColor(isWindowBackgroundDark())

fun Context.iconColor(color: Int): Int = iconColor(color.isDark)

fun Context.iconColor(dark: Boolean): Int = color(
    if (dark) {
        R.color.primary_text_default_material_dark
    } else {
        R.color.secondary_text_default_material_light
    }
)

fun ContextAware.iconColor(): Int =
    providedContext.iconColor()

fun ContextAware.iconColor(color: Int): Int =
    providedContext.iconColor(color)

fun ContextAware.iconColor(dark: Boolean): Int =
    providedContext.iconColor(dark)

fun View.iconColor(): Int =
    context.iconColor()

fun View.iconColor(color: Int): Int =
    context.iconColor(color)

fun View.iconColor(dark: Boolean): Int =
    context.iconColor(dark)

fun Context.rippleColor(): Int = rippleColor(isWindowBackgroundDark())

fun Context.rippleColor(color: Int): Int = rippleColor(color.isDark)

fun Context.rippleColor(dark: Boolean): Int = color(
    if (dark) {
        R.color.ripple_material_dark
    } else {
        R.color.ripple_material_light
    }
)

fun ContextAware.rippleColor(): Int = providedContext.rippleColor()

fun ContextAware.rippleColor(color: Int): Int =
    providedContext.rippleColor(color)

fun ContextAware.rippleColor(dark: Boolean): Int =
    providedContext.rippleColor(dark)

fun View.rippleColor(): Int =
    context.rippleColor()

fun View.rippleColor(color: Int): Int =
    context.rippleColor(color)

fun View.rippleColor(dark: Boolean): Int =
    context.rippleColor(dark)

fun Context.primaryTextColor(): Int = primaryTextColor(isWindowBackgroundDark())

fun Context.primaryTextColor(color: Int): Int = primaryTextColor(color.isDark)

fun Context.primaryTextColor(dark: Boolean): Int = color(
    if (dark) {
        R.color.primary_text_default_material_dark
    } else {
        R.color.primary_text_default_material_light
    }
)

fun ContextAware.primaryTextColor(): Int =
    providedContext.primaryTextColor()

fun ContextAware.primaryTextColor(color: Int): Int =
    providedContext.primaryTextColor(color)

fun ContextAware.primaryTextColor(dark: Boolean): Int =
    providedContext.primaryTextColor(dark)

fun View.primaryTextColor(): Int =
    context.primaryTextColor()

fun View.primaryTextColor(color: Int): Int =
    context.primaryTextColor(color)

fun View.primaryTextColor(dark: Boolean): Int =
    context.primaryTextColor(dark)

fun Context.primaryDisabledTextColor(): Int = primaryDisabledTextColor(isWindowBackgroundDark())

fun Context.primaryDisabledTextColor(color: Int): Int = primaryDisabledTextColor(color.isDark)

fun Context.primaryDisabledTextColor(dark: Boolean): Int = color(
    if (dark) {
        R.color.primary_text_disabled_material_dark
    } else {
        R.color.primary_text_disabled_material_light
    }
)

fun ContextAware.primaryDisabledTextColor(): Int =
    providedContext.primaryDisabledTextColor()

fun ContextAware.primaryDisabledTextColor(color: Int): Int =
    providedContext.primaryDisabledTextColor(color)

fun ContextAware.primaryDisabledTextColor(dark: Boolean): Int =
    providedContext.primaryDisabledTextColor(dark)

fun View.primaryDisabledTextColor(): Int =
    context.primaryDisabledTextColor()

fun View.primaryDisabledTextColor(color: Int): Int =
    context.primaryDisabledTextColor(color)

fun View.primaryDisabledTextColor(dark: Boolean): Int =
    context.primaryDisabledTextColor(dark)

fun Context.secondaryTextColor(): Int = secondaryTextColor(isWindowBackgroundDark())

fun Context.secondaryTextColor(color: Int): Int = secondaryTextColor(color.isDark)

fun Context.secondaryTextColor(dark: Boolean): Int = color(
    if (dark) {
        R.color.secondary_text_default_material_dark
    } else {
        R.color.secondary_text_default_material_light
    }
)

fun ContextAware.secondaryTextColor(): Int =
    providedContext.secondaryTextColor()

fun ContextAware.secondaryTextColor(color: Int): Int =
    providedContext.secondaryTextColor(color)

fun ContextAware.secondaryTextColor(dark: Boolean): Int =
    providedContext.secondaryTextColor(dark)

fun View.secondaryTextColor(): Int =
    context.secondaryTextColor()

fun View.secondaryTextColor(color: Int): Int =
    context.secondaryTextColor(color)

fun View.secondaryTextColor(dark: Boolean): Int =
    context.secondaryTextColor(dark)

fun Context.secondaryDisabledTextColor(): Int =
    secondaryDisabledTextColor(isWindowBackgroundDark())

fun Context.secondaryDisabledTextColor(color: Int): Int = secondaryDisabledTextColor(color.isDark)

fun Context.secondaryDisabledTextColor(dark: Boolean): Int = color(
    if (dark) {
        R.color.secondary_text_disabled_material_dark
    } else {
        R.color.secondary_text_disabled_material_light
    }
)

fun ContextAware.secondaryDisabledTextColor(): Int =
    providedContext.secondaryDisabledTextColor()

fun ContextAware.secondaryDisabledTextColor(color: Int): Int =
    providedContext.secondaryDisabledTextColor(color)

fun ContextAware.secondaryDisabledTextColor(dark: Boolean): Int =
    providedContext.secondaryDisabledTextColor(dark)

fun View.secondaryDisabledTextColor(): Int =
    context.secondaryDisabledTextColor()

fun View.secondaryDisabledTextColor(color: Int): Int =
    context.secondaryDisabledTextColor(color)

fun View.secondaryDisabledTextColor(dark: Boolean): Int =
    context.secondaryDisabledTextColor(dark)