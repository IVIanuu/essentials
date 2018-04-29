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
import android.os.Build
import android.widget.CheckBox
import com.ivianuu.essentials.R
import com.ivianuu.essentials.util.ext.getControlDisabledColor
import com.ivianuu.essentials.util.ext.getControlNormalColor
import com.ivianuu.essentials.util.ext.getTintedDrawable
import com.ivianuu.essentials.util.ext.isWindowBackgroundDark


fun CheckBox.tint(color: Int, isDark: Boolean = context.isWindowBackgroundDark) {
    val s1 = ColorStateList(
        arrayOf(
            intArrayOf(-android.R.attr.state_enabled),
            intArrayOf(android.R.attr.state_enabled, -android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_enabled, android.R.attr.state_checked)
        ),
        intArrayOf(
            context.getControlDisabledColor(isDark),
            context.getControlNormalColor(isDark),
            color
        )
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        buttonTintList = s1
    } else {
        buttonDrawable = context.getTintedDrawable(R.drawable.abc_btn_check_material, s1)
    }
}