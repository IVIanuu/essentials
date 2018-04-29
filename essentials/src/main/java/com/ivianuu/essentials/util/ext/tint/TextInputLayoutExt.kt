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
import android.support.design.widget.TextInputLayout
import com.ivianuu.essentials.util.ext.getField

fun TextInputLayout.setHintTextColor(hintColor: Int) {
    try {
        val mDefaultTextColorField =
            TextInputLayout::class.getField("mDefaultTextColor")
        mDefaultTextColorField.set(this, ColorStateList.valueOf(hintColor))
        val updateLabelStateMethod = TextInputLayout::class.java.getDeclaredMethod(
            "updateLabelState",
            Boolean::class.javaPrimitiveType,
            Boolean::class.javaPrimitiveType
        )
        updateLabelStateMethod.isAccessible = true
        updateLabelStateMethod.invoke(this, false, true)
    } catch (t: Throwable) {
        throw IllegalStateException(
            "Failed to set TextInputLayout hint (collapsed) color: " + t.localizedMessage, t
        )
    }
}

fun TextInputLayout.setAccentTextColor(accentColor: Int) {
    try {
        val mFocusedTextColorField =
            TextInputLayout::class.getField("mFocusedTextColor")
        mFocusedTextColorField.set(this, ColorStateList.valueOf(accentColor))
        val updateLabelStateMethod = TextInputLayout::class.java.getDeclaredMethod(
            "updateLabelState",
            Boolean::class.javaPrimitiveType,
            Boolean::class.javaPrimitiveType
        )
        updateLabelStateMethod.isAccessible = true
        updateLabelStateMethod.invoke(this, false, true)
    } catch (t: Throwable) {
        throw IllegalStateException(
            "Failed to set TextInputLayout accent (expanded) color: " + t.localizedMessage, t
        )
    }

}