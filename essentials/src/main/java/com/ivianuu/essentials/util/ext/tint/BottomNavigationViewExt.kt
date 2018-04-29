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

import android.R
import android.content.res.ColorStateList
import android.support.design.internal.BottomNavigationItemView
import android.support.design.widget.BottomNavigationView
import com.ivianuu.essentials.util.ext.*
import com.ivianuu.essentials.util.getBorderlessRippleDrawable

fun BottomNavigationView.tint(bgColor: Int,
                              selectedColor: Int = context.getIconColor(bgColor.isDark),
                              unselectedColor: Int = context.getInactiveIconColor(bgColor.isDark)) {
    setBackgroundColor(bgColor)
    setItemColor(selectedColor, unselectedColor)
    setItemRippleColor(bgColor)
}

fun BottomNavigationView.setItemColor(
    selectedColor: Int = context.getIconColor(),
    unselectedColor: Int = context.getInactiveIconColor()) {

    val colorStateList = ColorStateList(
        arrayOf(
            intArrayOf(-R.attr.state_checked),
            intArrayOf(R.attr.state_checked)
        ),
        intArrayOf(unselectedColor, selectedColor)
    )

    itemIconTintList = colorStateList
    itemTextColor = colorStateList

}

fun BottomNavigationView.setItemRippleColor(color: Int = context.getRippleColor()) {
    try {
        val menuViewField = BottomNavigationView::class.getField("mMenuView")
        val menuView = menuViewField.get(this)
        val buttonsField = menuView::class.getField("mButtons")
        val buttons = buttonsField.get(menuView) as Array<BottomNavigationItemView>
        buttons.forEach {
            it.background = getBorderlessRippleDrawable(context, color)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}