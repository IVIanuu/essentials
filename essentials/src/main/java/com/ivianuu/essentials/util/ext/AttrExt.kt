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

package com.ivianuu.essentials.util.ext

import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build

fun Context.resolveBooleanAttr(attr: Int): Boolean = resolveBooleanAttr(attr, false)

fun Context.resolveBooleanAttr(attr: Int, defaultValue: Boolean): Boolean {
    val array = getTypedArrayWithAttributes(attr)
    val bool = array.getBoolean(0, defaultValue)
    array.recycle()
    return bool
}

fun Context.resolveColorAttr(attr: Int): Int = resolveColorAttr(attr, -1)

fun Context.resolveColorAttr(attr: Int, defaultValue: Int): Int {
    val array = getTypedArrayWithAttributes(attr)
    val color = array.getColor(0, defaultValue)
    array.recycle()
    return color
}

fun Context.resolveColorStateListAttr(attr: Int): ColorStateList? =
    resolveColorStateListAttr(attr, null)

fun Context.resolveColorStateListAttr(attr: Int,
                                      defaultValue: ColorStateList?): ColorStateList? {
    val array = getTypedArrayWithAttributes(attr)
    val colorStateList = array.getColorStateList(0)
    array.recycle()
    return colorStateList ?: defaultValue
}


fun Context.resolveDimenAttr(attr: Int): Float = resolveDimenAttr(attr, -1f)

fun Context.resolveDimenAttr(attr: Int, defaultValue: Float): Float {
    val array = getTypedArrayWithAttributes(attr)
    val dimension = array.getDimension(0, defaultValue)
    array.recycle()
    return dimension
}

fun Context.resolveDimenPxOffsetAttr(attr: Int): Int =
    resolveDimenPxOffsetAttr(attr, -1)

fun Context.resolveDimenPxOffsetAttr(attr: Int, defaultValue: Int): Int {
    val array = getTypedArrayWithAttributes(attr)
    val dimension = array.getDimensionPixelOffset(0, defaultValue)
    array.recycle()
    return dimension
}

fun Context.resolveDimenPxAttr(attr: Int): Int =
    resolveDimenPxAttr(attr, -1)

fun Context.resolveDimenPxAttr(attr: Int, defaultValue: Int): Int {
    val array = getTypedArrayWithAttributes(attr)
    val dimension = array.getDimensionPixelSize(0, defaultValue)
    array.recycle()
    return dimension
}

fun Context.resolveDrawableAttr(attr: Int): Drawable? = resolveDrawableAttr(attr, null)

fun Context.resolveDrawableAttr(attr: Int, defaultValue: Drawable?): Drawable? {
    val array = getTypedArrayWithAttributes(attr)
    val drawable = array.getDrawable(0)
    array.recycle()
    return drawable ?: defaultValue
}

fun Context.resolveFloatAttr(attr: Int): Float = resolveFloatAttr(attr, -1f)

fun Context.resolveFloatAttr(attr: Int, defaultValue: Float): Float {
    val array = getTypedArrayWithAttributes(attr)
    val floatValue = array.getFloat(0, defaultValue)
    array.recycle()
    return floatValue
}

fun Context.resolveFontAttr(attr: Int): Typeface? = resolveFontAttr(attr, null)

@TargetApi(Build.VERSION_CODES.O)
fun Context.resolveFontAttr(attr: Int, defaultValue: Typeface?): Typeface? {
    val array = getTypedArrayWithAttributes(attr)
    val font = array.getFont(0)
    array.recycle()
    return font ?: defaultValue
}

fun Context.resolveIntAttr(attr: Int): Int = resolveIntAttr(attr, -1)

fun Context.resolveIntAttr(attr: Int, defaultValue: Int): Int {
    val array = getTypedArrayWithAttributes(attr)
    val intValue = array.getInt(0, defaultValue)
    array.recycle()
    return intValue
}

fun Context.resolveIntegerAttr(attr: Int): Int = resolveIntegerAttr(attr, -1)

fun Context.resolveIntegerAttr(attr: Int, defaultValue: Int?): Int {
    val array = getTypedArrayWithAttributes(attr)
    val integer = array.getInteger(0, defaultValue!!)
    array.recycle()
    return integer
}

fun Context.resolveStringAttr(attr: Int): String? = resolveStringAttr(attr, null)

fun Context.resolveStringAttr(attr: Int, defaultValue: String?): String? {
    val array = getTypedArrayWithAttributes(attr)
    val string = array.getString(0)
    array.recycle()
    return string ?: defaultValue
}

fun Context.resolveTextAttr(attr: Int): CharSequence? = resolveStringAttr(attr, null)

fun Context.resolveTextAttr(attr: Int, defaultValue: CharSequence?): CharSequence? {
    val array = getTypedArrayWithAttributes(attr)
    val charSequence = array.getText(0)
    array.recycle()
    return charSequence ?: defaultValue
}

fun Context.resolveTextArrayAttr(attr: Int): Array<CharSequence>? =
    resolveTextArrayAttr(attr, null)

fun Context.resolveTextArrayAttr(attr: Int,
                                 defaultValue: Array<CharSequence>?): Array<CharSequence>? {
    val array = getTypedArrayWithAttributes(attr)
    val charSequence = array.getTextArray(0)
    array.recycle()
    return charSequence ?: defaultValue
}

fun Context.getTypedArrayWithAttributes(vararg attr: Int): TypedArray =
    theme.obtainStyledAttributes(attr)