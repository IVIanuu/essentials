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

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Build
import android.widget.RadioButton
import com.ivianuu.essentials.R
import com.ivianuu.essentials.util.ext.*

@SuppressLint("PrivateResource")
fun RadioButton.tint(color: Int, isDark: Boolean = context.isWindowBackgroundDark) {
    val s1 = ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_enabled),
            intArrayOf(android.R.attr.state_enabled, -android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_enabled, android.R.attr.state_checked)
        ),
        intArrayOf(
            // Rdio button includes own alpha for disabled state
            context.getControlDisabledColor(isDark).stripAlpha(),
            context.getControlNormalColor(isDark),
            color
        )
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        buttonTintList = s1
    } else {
        buttonDrawable = context.getTintedDrawable(R.drawable.abc_btn_radio_material, s1)
    }
}