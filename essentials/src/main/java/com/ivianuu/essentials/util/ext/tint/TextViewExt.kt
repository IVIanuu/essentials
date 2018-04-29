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

import android.graphics.drawable.Drawable
import android.widget.TextView
import com.ivianuu.essentials.util.ext.getField
import com.ivianuu.essentials.util.ext.getResDrawable
import com.ivianuu.essentials.util.ext.getTintedDrawable
import com.ivianuu.essentials.util.ext.tinted

fun TextView.tint(color: Int) {
    setCursorTint(color)
    setTextHandleTint(color)
}

fun TextView.setCursorTint(color: Int) {
    try {
        val fCursorDrawableRes = TextView::class.getField("mCursorDrawableRes")
        val mCursorDrawableRes = fCursorDrawableRes.getInt(this)
        val fEditor = TextView::class.getField("mEditor")
        val editor = fEditor.get(this)
        val fCursorDrawable = editor::class.getField("mCursorDrawable")
        val drawables = arrayOfNulls<Drawable>(2)
        drawables[0] = context.getTintedDrawable(mCursorDrawableRes, color)
        drawables[1] = context.getTintedDrawable(mCursorDrawableRes, color)
        fCursorDrawable.set(editor, drawables)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun TextView.setTextHandleTint(color: Int) {
    try {
        val editorField = TextView::class.getField("mEditor")
        val editor = editorField.get(this)
        val editorClass = editor::class

        val handleNames = arrayOf(
            "mSelectHandleLeft", "mSelectHandleRight",
            "mSelectHandleCenter"
        )
        val resNames = arrayOf(
            "mTextSelectHandleLeftRes", "mTextSelectHandleRightRes",
            "mTextSelectHandleRes"
        )

        for (i in handleNames.indices) {
            val handleField = editorClass.getField(handleNames[i])

            var handleDrawable: Drawable? = handleField.get(editor) as Drawable?

            if (handleDrawable == null) {
                val resField = TextView::class.getField(resNames[i])
                val resId = resField.getInt(this)
                handleDrawable = context.getResDrawable(resId)
            }

            handleField.set(editor, handleDrawable.tinted(color))
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}