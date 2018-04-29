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

import android.support.design.widget.TabLayout
import android.view.View
import com.ivianuu.essentials.util.ColorStateListUtil.getDisabledColorStateList
import com.ivianuu.essentials.util.ext.*
import com.ivianuu.essentials.util.getRippleDrawable

fun TabLayout.tint(bgColor: Int,
                   activeColor: Int = context.getIconColor(bgColor.isDark),
                   inactiveColor: Int = context.getInactiveIconColor(bgColor.isDark)) {
    setBackgroundColor(bgColor)

    (0 until tabCount).mapNotNull { getTabAt(it) }
        .forEach {
            it.icon = it.icon.tintedNullable(getDisabledColorStateList(activeColor, inactiveColor))
            try {
                val viewField = it::class.getField("mView")
                val tabView = viewField.get(it) as View
                tabView.background = getRippleDrawable(context, bgColor.isDark)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    setTabTextColors(inactiveColor, activeColor)
}