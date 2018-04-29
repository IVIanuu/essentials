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

import android.os.Build
import android.widget.SeekBar
import com.ivianuu.essentials.util.ColorStateListUtil.getDisabledColorStateList
import com.ivianuu.essentials.util.ext.getControlDisabledColor
import com.ivianuu.essentials.util.ext.isWindowBackgroundDark
import com.ivianuu.essentials.util.ext.tintedNullable

fun SeekBar.tint(color: Int, isDark: Boolean = context.isWindowBackgroundDark) {
    val s1 = getDisabledColorStateList(
        color,
        context.getControlDisabledColor(isDark)
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        thumbTintList = s1
        progressTintList = s1
    } else {
        progressDrawable = progressDrawable.tintedNullable(s1)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            thumb = thumb.tintedNullable(s1)
        }
    }
}