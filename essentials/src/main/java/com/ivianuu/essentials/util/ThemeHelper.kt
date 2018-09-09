package com.ivianuu.essentials.util

import android.annotation.SuppressLint
import com.ivianuu.essentials.R
import com.ivianuu.essentials.util.ext.isDark
import javax.inject.Inject

/**
 * Theme helper
 */
@SuppressLint("PrivateResource")
class ThemeHelper @Inject constructor(
    private val attributeProvider: AttributeProvider,
    private val resourceProvider: ResourceProvider
) {

    fun isWindowBackgroundDark() =
        attributeProvider.color(android.R.attr.windowBackground).isDark

    fun primaryColor() = attributeProvider.color(android.R.attr.colorPrimary)

    fun primaryColorDark() = attributeProvider.color(android.R.attr.colorPrimaryDark)

    fun accentColor() = attributeProvider.color(android.R.attr.colorAccent)

    fun cardColor() = cardColor(isWindowBackgroundDark())

    fun cardColor(color: Int) = cardColor(color.isDark)

    fun cardColor(dark: Boolean) = resourceProvider.color(
        if (dark) {
            R.color.cardview_dark_background
        } else {
            R.color.cardview_light_background
        }
    )

    fun iconColor() = iconColor(isWindowBackgroundDark())

    fun iconColor(color: Int) = iconColor(color.isDark)

    fun iconColor(dark: Boolean) = resourceProvider.color(
        if (dark) {
            R.color.primary_text_default_material_dark
        } else {
            R.color.secondary_text_default_material_light
        }
    )

    fun rippleColor() = rippleColor(isWindowBackgroundDark())

    fun rippleColor(color: Int) = rippleColor(color.isDark)

    fun rippleColor(dark: Boolean) = resourceProvider.color(
        if (dark) {
            R.color.ripple_material_dark
        } else {
            R.color.ripple_material_light
        }
    )

    fun primaryTextColor() = primaryTextColor(isWindowBackgroundDark())

    fun primaryTextColor(color: Int) = primaryTextColor(color.isDark)

    fun primaryTextColor(dark: Boolean) = resourceProvider.color(
        if (dark) {
            R.color.primary_text_default_material_dark
        } else {
            R.color.primary_text_default_material_light
        }
    )

    fun primaryDisabledTextColor() = primaryDisabledTextColor(isWindowBackgroundDark())

    fun primaryDisabledTextColor(color: Int) = primaryDisabledTextColor(color.isDark)

    fun primaryDisabledTextColor(dark: Boolean) = resourceProvider.color(
        if (dark) {
            R.color.primary_text_disabled_material_dark
        } else {
            R.color.primary_text_disabled_material_light
        }
    )

    fun secondaryTextColor() = secondaryTextColor(isWindowBackgroundDark())

    fun secondaryTextColor(color: Int) = secondaryTextColor(color.isDark)

    fun secondaryTextColor(dark: Boolean) = resourceProvider.color(
        if (dark) {
            R.color.secondary_text_default_material_dark
        } else {
            R.color.secondary_text_default_material_light
        }
    )

    fun secondaryDisabledTextColor() =
        secondaryDisabledTextColor(isWindowBackgroundDark())

    fun secondaryDisabledTextColor(color: Int) = secondaryDisabledTextColor(color.isDark)

    fun secondaryDisabledTextColor(dark: Boolean) = resourceProvider.color(
        if (dark) {
            R.color.secondary_text_disabled_material_dark
        } else {
            R.color.secondary_text_disabled_material_light
        }
    )
}