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

import android.content.res.ColorStateList
import android.graphics.Color
import android.widget.Button
import com.ivianuu.essentials.util.ext.*

fun Button.tint(color: Int,
                isDark: Boolean = context.isWindowBackgroundDark,
                isBorderless: Boolean = false) {

    if (!isBorderless) {
        tintBackground(color, isDark)
    }

    var enabled = if (color.isLight) Color.BLACK else Color.WHITE
    var disabled = if (isDark) Color.WHITE else Color.BLACK

    if (isBorderless) {
        // Invert of a normal/disabled control
        enabled = context.getControlDisabledColor(isDark).stripAlpha()
        disabled = context.getControlNormalColor(isDark)
    }

    val textColorSl = ColorStateList(
        arrayOf(
            intArrayOf(android.R.attr.state_enabled),
            intArrayOf(-android.R.attr.state_enabled)
        ),
        intArrayOf(enabled, disabled)
    )

    setTextColor(textColorSl)

    // Hack around button color not updating
    isEnabled = !isEnabled
    isEnabled = !isEnabled
}