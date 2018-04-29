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
import android.content.res.ColorStateList
import com.ivianuu.essentials.R

val Context.isWindowBackgroundDark
    get() = resolveColorAttr(android.R.attr.windowBackground).isDark

fun Context.getPrimaryColor() = resolveColorAttr(android.R.attr.colorPrimary)

fun Context.getPrimaryColorDark() = resolveColorAttr(android.R.attr.colorPrimaryDark)

fun Context.getAccentColor() = resolveColorAttr(android.R.attr.colorAccent)

fun Context.getButtonDisabledColor(isDark: Boolean = isWindowBackgroundDark): Int {
    return getResColor(
        if (isDark) {
            R.color.md_button_disabled_dark
        } else {
            R.color.md_button_disabled_light
        }
    )
}

fun Context.getButtonTextDisabledColor(isDark: Boolean = isWindowBackgroundDark): Int {
    return getResColor(
        if (isDark) {
            R.color.md_button_text_disabled_dark
        } else {
            R.color.md_button_text_disabled_light
        }
    )
}

fun Context.getCardColor(isDark: Boolean = isWindowBackgroundDark): Int {
    return getResColor(
        if (isDark) {
            R.color.md_cardview_bg_dark
        } else {
            R.color.md_cardview_bg_light
        }
    )
}

fun Context.getControlDisabledColor(isDark: Boolean = isWindowBackgroundDark): Int {
    return getResColor(
        if (isDark) {
            R.color.md_control_disabled_dark
        } else {
            R.color.md_control_disabled_light
        }
    )
}

fun Context.getControlNormalColor(isDark: Boolean = isWindowBackgroundDark): Int {
    return getResColor(
        if (isDark) {
            R.color.md_control_normal_dark
        } else {
            R.color.md_control_normal_light
        }
    )
}

fun Context.getTextDisabledColor(isDark: Boolean = isWindowBackgroundDark): Int {
    return getResColor(
        if (isDark) {
            R.color.md_text_disabled_dark
        } else {
            R.color.md_text_disabled_light
        }
    )
}

fun Context.getNavigationDrawerSelectedColor(isDark: Boolean = isWindowBackgroundDark): Int {
    return getResColor(
        if (isDark) {
            R.color.md_navigation_drawer_selected_dark
        } else {
            R.color.md_navigation_drawer_selected_light
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

fun Context.getIconColor(isDark: Boolean = isWindowBackgroundDark): Int {
    return getResColor(
        if (isDark) {
            R.color.md_icon_dark
        } else {
            R.color.md_icon_light
        }
    )
}

fun Context.getInactiveIconColor(isDark: Boolean = isWindowBackgroundDark): Int {
    return getResColor(
        if (isDark) {
            R.color.md_icon_dark_inactive
        } else {
            R.color.md_icon_light_inactive
        }
    )
}

fun Context.getBottomNavBgColor(isDark: Boolean = isWindowBackgroundDark): Int {
    return getResColor(
        if (isDark) {
            R.color.md_bottom_nav_default_dark_bg
        } else {
            R.color.md_bottom_nav_default_light_bg
        }
    )
}

fun Context.getSwitchThumbDisabledColor(isDark: Boolean = isWindowBackgroundDark): Int {
    return getResColor(
        if (isDark) {
            R.color.switch_thumb_disabled_material_dark
        } else {
            R.color.switch_thumb_disabled_material_light
        }
    )
}

fun Context.getSwitchThumbNormalColor(isDark: Boolean = isWindowBackgroundDark): Int {
    return getResColor(
        if (isDark) {
            R.color.switch_thumb_normal_material_dark
        } else {
            R.color.switch_thumb_normal_material_light
        }
    )
}

fun Context.getSwitchTrackDisabledColor(isDark: Boolean = isWindowBackgroundDark): Int {
    return getResColor(
        if (isDark) {
            R.color.md_switch_track_disabled_dark
        } else {
            R.color.md_switch_track_disabled_light
        }
    )
}

fun Context.getSwitchTrackNormalColor(isDark: Boolean = isWindowBackgroundDark): Int {
    return getResColor(
        if (isDark) {
            R.color.md_switch_track_normal_dark
        } else {
            R.color.md_switch_track_normal_light
        }
    )
}

fun Context.getTintedDrawable(resId: Int, color: Int) = getResDrawable(resId).tinted(color)

fun Context.getTintedDrawable(resId: Int, colorStateList: ColorStateList) =
    getResDrawable(resId).tinted(colorStateList)