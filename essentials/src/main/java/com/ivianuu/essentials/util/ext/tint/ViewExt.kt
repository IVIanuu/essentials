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
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.os.Build
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.TextInputEditText
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.ivianuu.essentials.util.ColorStateListUtil.getDisabledColorStateList
import com.ivianuu.essentials.util.ext.*

fun View.tintBackground(color: Int, isDark: Boolean = context.isWindowBackgroundDark) {
    if (this is FloatingActionButton || this is Button) {
        setTintSelector(color, isDark)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && background is RippleDrawable) {
        val ripple = this.background as RippleDrawable
        val unchecked = context.getRippleColor(isDark)
        val checked = color.adjustAlpha(0.4f)
        val sl = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_activated, -android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_activated),
                intArrayOf(android.R.attr.state_checked)
            ),
            intArrayOf(unchecked, checked, checked)
        )
        ripple.setColor(sl)
    } else {
        val drawable: Drawable? = this.background
        if (drawable != null) {
            if (this is TextInputEditText) {
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            } else {
                setBackgroundCompat(drawable.tinted(color))
            }
        }
    }
}

fun View.setTintSelector(color: Int, isDark: Boolean) {
    val isColorLight = color.isLight
    val disabled = context.getButtonDisabledColor(isDark)
    val pressed = color.shift(if (isDark) 0.9f else 1.1f)
    val activated = color.shift(if (isDark) 1.1f else 0.9f)
    val rippleColor = context.getRippleColor(!isColorLight)
    val textColor = context.getPrimaryTextColor(isDark)

    val s1: ColorStateList
    when {
        this is Button -> {
            s1 = getDisabledColorStateList(color, disabled)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && background is RippleDrawable) {
                val rd = background as RippleDrawable
                rd.setColor(ColorStateList.valueOf(rippleColor))
            }

            // Disabled text color state for buttons, may get overridden later by ATE tags
            setTextColor(
                getDisabledColorStateList(
                    textColor,
                    context.getButtonTextDisabledColor(isDark)
                )
            )
        }
        this is FloatingActionButton -> {
            // FloatingActionButton doesn't support disabled state?
            s1 = ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_pressed),
                    intArrayOf(android.R.attr.state_pressed)
                ),
                intArrayOf(color, pressed)
            )

            this.rippleColor = rippleColor
            backgroundTintList = s1
            drawable?.let {
                setImageDrawable(it.tinted(textColor))
            }
            return
        }
        else ->
            s1 = ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_enabled),
                    intArrayOf(android.R.attr.state_enabled),
                    intArrayOf(android.R.attr.state_enabled, android.R.attr.state_pressed),
                    intArrayOf(android.R.attr.state_enabled, android.R.attr.state_activated),
                    intArrayOf(android.R.attr.state_enabled, android.R.attr.state_checked)
                ),
                intArrayOf(disabled, color, pressed, activated, activated)
            )
    }

    background?.let { setBackgroundCompat(it.tinted(s1)) }

    if (this is TextView && this !is Button) {
        setTextColor(
            getDisabledColorStateList(
                textColor,
                context.getTextDisabledColor(isDark)
            )
        )
    }
}

fun View.setBackgroundCompat(drawable: Drawable?) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        background = drawable
    } else {
        setBackgroundDrawable(drawable)
    }
}