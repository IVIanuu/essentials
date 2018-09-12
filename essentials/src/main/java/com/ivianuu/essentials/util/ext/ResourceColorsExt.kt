@file:SuppressLint("PrivateResource")
@file:Suppress("NOTHING_TO_INLINE")

// Aliases to other public API.

package com.ivianuu.essentials.util.ext

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import com.ivianuu.essentials.R
import com.ivianuu.essentials.util.ContextAware

inline fun Context.isWindowBackgroundDark() =
    colorAttr(android.R.attr.windowBackground).isDark

inline fun ContextAware.isWindowBackgroundDark() =
    providedContext.isWindowBackgroundDark()

inline fun Fragment.isWindowBackgroundDark() = requireContext().isWindowBackgroundDark()

inline fun View.isWindowBackgroundDark() = context.isWindowBackgroundDark()

inline fun Context.primaryColor() = colorAttr(android.R.attr.colorPrimary)

inline fun ContextAware.primaryColor() =
    providedContext.colorAttr(android.R.attr.colorPrimary)

inline fun Fragment.primaryColor() =
    requireContext().colorAttr(android.R.attr.colorPrimary)

inline fun View.primaryColor() =
    context.colorAttr(android.R.attr.colorPrimary)

inline fun Context.primaryColorDark() = colorAttr(android.R.attr.colorPrimaryDark)

inline fun ContextAware.primaryColorDark() =
    providedContext.primaryColorDark()

inline fun Fragment.primaryColorDark() =
    requireContext().primaryColorDark()

inline fun View.primaryColorDark() =
    context.primaryColorDark()

inline fun Context.accentColor() = colorAttr(android.R.attr.colorAccent)

inline fun ContextAware.accentColor() =
    providedContext.accentColor()

inline fun Fragment.accentColor() =
    requireContext().accentColor()

inline fun View.accentColor() =
    context.accentColor()

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

inline fun Fragment.cardColor() =
    requireContext().cardColor()

inline fun Fragment.cardColor(color: Int) =
    requireContext().cardColor(color)

inline fun Fragment.cardColor(dark: Boolean) =
    requireContext().cardColor(dark)

inline fun View.cardColor() =
    context.cardColor()

inline fun View.cardColor(color: Int) =
    context.cardColor(color)

inline fun View.cardColor(dark: Boolean) =
    context.cardColor(dark)

inline fun Context.iconColor() = iconColor(isWindowBackgroundDark())

inline fun Context.iconColor(color: Int) = iconColor(color.isDark)

inline fun Context.iconColor(dark: Boolean) = color(
    if (dark) {
        R.color.primary_text_default_material_dark
    } else {
        R.color.secondary_text_default_material_light
    }
)

inline fun ContextAware.iconColor() =
    providedContext.iconColor()

inline fun ContextAware.iconColor(color: Int) =
    providedContext.iconColor(color)

inline fun ContextAware.iconColor(dark: Boolean) =
    providedContext.iconColor(dark)

inline fun Fragment.iconColor() =
    requireContext().iconColor()

inline fun Fragment.iconColor(color: Int) =
    requireContext().iconColor(color)

inline fun Fragment.iconColor(dark: Boolean) =
    requireContext().iconColor(dark)

inline fun View.iconColor() =
    context.iconColor()

inline fun View.iconColor(color: Int) =
    context.iconColor(color)

inline fun View.iconColor(dark: Boolean) =
    context.iconColor(dark)

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

inline fun Fragment.rippleColor() =
    requireContext().rippleColor()

inline fun Fragment.rippleColor(color: Int) =
    requireContext().rippleColor(color)

inline fun Fragment.rippleColor(dark: Boolean) =
    requireContext().rippleColor(dark)

inline fun View.rippleColor() =
    context.rippleColor()

inline fun View.rippleColor(color: Int) =
    context.rippleColor(color)

inline fun View.rippleColor(dark: Boolean) =
    context.rippleColor(dark)

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

inline fun Fragment.primaryTextColor() =
    requireContext().primaryTextColor()

inline fun Fragment.primaryTextColor(color: Int) =
    requireContext().primaryTextColor(color)

inline fun Fragment.primaryTextColor(dark: Boolean) =
    requireContext().primaryTextColor(dark)

inline fun View.primaryTextColor() =
    context.primaryTextColor()

inline fun View.primaryTextColor(color: Int) =
    context.primaryTextColor(color)

inline fun View.primaryTextColor(dark: Boolean) =
    context.primaryTextColor(dark)

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

inline fun Fragment.primaryDisabledTextColor() =
    requireContext().primaryDisabledTextColor()

inline fun Fragment.primaryDisabledTextColor(color: Int) =
    requireContext().primaryDisabledTextColor(color)

inline fun Fragment.primaryDisabledTextColor(dark: Boolean) =
    requireContext().primaryDisabledTextColor(dark)

inline fun View.primaryDisabledTextColor() =
    context.primaryDisabledTextColor()

inline fun View.primaryDisabledTextColor(color: Int) =
    context.primaryDisabledTextColor(color)

inline fun View.primaryDisabledTextColor(dark: Boolean) =
    context.primaryDisabledTextColor(dark)

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

inline fun Fragment.secondaryTextColor() =
    requireContext().secondaryTextColor()

inline fun Fragment.secondaryTextColor(color: Int) =
    requireContext().secondaryTextColor(color)

inline fun Fragment.secondaryTextColor(dark: Boolean) =
    requireContext().secondaryTextColor(dark)

inline fun View.secondaryTextColor() =
    context.secondaryTextColor()

inline fun View.secondaryTextColor(color: Int) =
    context.secondaryTextColor(color)

inline fun View.secondaryTextColor(dark: Boolean) =
    context.secondaryTextColor(dark)

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

inline fun Fragment.secondaryDisabledTextColor() =
    requireContext().secondaryDisabledTextColor()

inline fun Fragment.secondaryDisabledTextColor(color: Int) =
    requireContext().secondaryDisabledTextColor(color)

inline fun Fragment.secondaryDisabledTextColor(dark: Boolean) =
    requireContext().secondaryDisabledTextColor(dark)

inline fun View.secondaryDisabledTextColor() =
    context.secondaryDisabledTextColor()

inline fun View.secondaryDisabledTextColor(color: Int) =
    context.secondaryDisabledTextColor(color)

inline fun View.secondaryDisabledTextColor(dark: Boolean) =
    context.secondaryDisabledTextColor(dark)