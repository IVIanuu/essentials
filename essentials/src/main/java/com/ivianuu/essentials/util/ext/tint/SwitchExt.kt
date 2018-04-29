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

package com.ivianuu.essentials.util.ext.tint

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.widget.Switch
import com.ivianuu.essentials.util.ext.*

fun Switch.tint(color: Int, isDark: Boolean = context.isWindowBackgroundDark) {
    trackDrawable?.let {
        trackDrawable = modifySwitchDrawable(
            context,
            it,
            color,
            false,
            false,
            isDark
        )
    }
    thumbDrawable?.let {
        thumbDrawable = modifySwitchDrawable(
            context,
            it,
            color,
            true,
            false,
            isDark
        )
    }
}

fun modifySwitchDrawable(
    context: Context,
    from: Drawable,
    tint: Int,
    thumb: Boolean,
    compatSwitch: Boolean,
    isDark: Boolean
): Drawable? {
    var tint = tint
    if (isDark) {
        tint = tint.lighten()
    }
    tint = tint.adjustAlpha(if (compatSwitch && !thumb) 0.5f else 1.0f)
    val disabled: Int
    var normal: Int
    if (thumb) {
        disabled = context.getSwitchThumbDisabledColor(isDark)
        normal = context.getSwitchThumbNormalColor(isDark)
    } else {
        disabled = context.getSwitchTrackDisabledColor(isDark)
        normal = context.getSwitchTrackNormalColor(isDark)
    }

    // Stock switch includes its own alpha
    if (!compatSwitch) {
        normal = normal.stripAlpha()
    }

    val sl = ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_enabled),
            intArrayOf(
                android.R.attr.state_enabled,
                -android.R.attr.state_activated,
                -android.R.attr.state_checked
            ),
            intArrayOf(android.R.attr.state_enabled, android.R.attr.state_activated),
            intArrayOf(android.R.attr.state_enabled, android.R.attr.state_checked)
        ),
        intArrayOf(disabled, normal, tint, tint)
    )
    return from.tinted(sl)
}