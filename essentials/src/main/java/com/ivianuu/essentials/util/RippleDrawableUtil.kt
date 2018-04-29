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

package com.ivianuu.essentials.util

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import com.ivianuu.essentials.util.ext.getRippleColor
import com.ivianuu.essentials.util.ext.isDark

object RippleDrawableUtil {

    fun getRippleDrawable(context: Context, color: Int): Drawable {
        return getRippleDrawable(
            context,
            color.isDark
        )
    }

    fun getRippleDrawable(context: Context, dark: Boolean): Drawable {
        return getRippleDrawable(
            context.getRippleColor(
                dark
            )
        )
    }

    fun getRippleDrawable(color: Int): Drawable {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RippleDrawable(
                ColorStateList.valueOf(color),
                null,
                ColorDrawable(Color.WHITE)
            )
        } else {
            getStateListDrawable(color)
        }
    }

    fun getBorderlessRippleDrawable(context: Context, color: Int): Drawable {
        return getBorderlessRippleDrawable(
            context,
            color.isDark
        )
    }

    fun getBorderlessRippleDrawable(context: Context, dark: Boolean): Drawable {
        return getBorderlessRippleDrawable(
            context.getRippleColor(dark)
        )
    }

    fun getBorderlessRippleDrawable(color: Int): Drawable {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            RippleDrawable(
                ColorStateList.valueOf(color),
                null,
                null
            )
        } else {
            getStateListDrawable(color)
        }
    }

    private fun getStateListDrawable(
        color: Int
    ): StateListDrawable {
        val states = StateListDrawable()
        states.addState(
            intArrayOf(android.R.attr.state_pressed),
            ColorDrawable(color)
        )
        states.addState(
            intArrayOf(android.R.attr.state_focused),
            ColorDrawable(color)
        )
        states.addState(
            intArrayOf(android.R.attr.state_activated),
            ColorDrawable(color)
        )
        states.addState(
            intArrayOf(),
            ColorDrawable(Color.TRANSPARENT)
        )
        return states
    }

}