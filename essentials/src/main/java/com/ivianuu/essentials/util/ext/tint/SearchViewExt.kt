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
import android.support.v7.widget.SearchView
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import com.ivianuu.essentials.util.ColorStateListUtil.getEnabledColorStateList
import com.ivianuu.essentials.util.ext.*
import java.lang.reflect.Field

fun SearchView.tint(bgColor: Int,
                    activeColor: Int = context.getIconColor(bgColor.isDark),
                    inactiveColor: Int = context.getInactiveIconColor(bgColor.isDark)) {
    setBackgroundColor(bgColor)
    setItemColor(activeColor, inactiveColor)
}

fun SearchView.setItemColor(activeColor: Int = context.getIconColor(),
                            inactiveColor: Int = context.getInactiveIconColor()) {
    val colorStateList = getEnabledColorStateList(
        activeColor,
        inactiveColor
    )

    try {
        val searchSrcTextViewField = SearchView::class.getField("mSearchSrcTextView")
        val searchSrcTextView = searchSrcTextViewField.get(this) as EditText
        searchSrcTextView.setTextColor(activeColor)
        searchSrcTextView.setHintTextColor(inactiveColor)
        searchSrcTextView.setCursorTint(activeColor)

        var field = SearchView::class.getField("mSearchButton")
        tintImageView(field, activeColor, inactiveColor)
        field = SearchView::class.getField("mGoButton")
        tintImageView(field, activeColor, inactiveColor)
        field = SearchView::class.getField("mCloseButton")
        tintImageView(field, activeColor, inactiveColor)
        field = SearchView::class.getField("mVoiceButton")
        tintImageView(field, activeColor, inactiveColor)

        field = SearchView::class.getField("mSearchPlate")
        (field.get(this) as View).tintBackground(
            activeColor,
            activeColor.isLight
        )

        field = SearchView::class.getField("mSearchHintIcon")
        field.set(this, (field.get(this) as Drawable?)
            .tintedNullable(colorStateList))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun SearchView.tintImageView(field: Field, activeColor: Int, inactiveColor: Int) {
    val imageView = field.get(this) as ImageView
    if (imageView.drawable != null) {
        imageView.setImageDrawable(
            imageView.drawable.tintedNullable(getEnabledColorStateList(activeColor, inactiveColor))
        )
    }
}