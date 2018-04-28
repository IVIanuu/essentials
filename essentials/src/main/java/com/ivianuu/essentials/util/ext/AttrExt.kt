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
import android.support.v4.app.Fragment
import android.view.View

fun Context.resolveBooleanAttr(attr: Int, defaultValue: Boolean = false): Boolean {
    val array = getTypedArrayWithAttributes(attr)
    val bool = array.getBoolean(0, defaultValue)
    array.recycle()
    return bool
}

fun Context.resolveColorAttr(attr: Int, defaultValue: Int = 0): Int {
    val array = getTypedArrayWithAttributes(attr)
    val color = array.getColor(0, defaultValue)
    array.recycle()
    return color
}

fun Context.resolveColorStateListAttr(attr: Int,
                                      defaultValue: ColorStateList? = null): ColorStateList? {
    val array = getTypedArrayWithAttributes(attr)
    val colorStateList = array.getColorStateList(0)
    array.recycle()
    return colorStateList ?: defaultValue
}

fun Context.resolveDimenAttr(attr: Int, defaultValue: Float = 0f): Float {
    val array = getTypedArrayWithAttributes(attr)
    val dimension = array.getDimension(0, defaultValue)
    array.recycle()
    return dimension
}

fun Context.resolveDimenPxOffsetAttr(attr: Int, defaultValue: Int = 0): Int {
    val array = getTypedArrayWithAttributes(attr)
    val dimension = array.getDimensionPixelOffset(0, defaultValue)
    array.recycle()
    return dimension
}

fun Context.resolveDimenPxAttr(attr: Int, defaultValue: Int = 0): Int {
    val array = getTypedArrayWithAttributes(attr)
    val dimension = array.getDimensionPixelSize(0, defaultValue)
    array.recycle()
    return dimension
}

fun Context.resolveDrawableAttr(attr: Int, defaultValue: Drawable? = null): Drawable? {
    val array = getTypedArrayWithAttributes(attr)
    val drawable = array.getDrawable(0)
    array.recycle()
    return drawable ?: defaultValue
}

fun Context.resolveFloatAttr(attr: Int, defaultValue: Float = 0f): Float {
    val array = getTypedArrayWithAttributes(attr)
    val floatValue = array.getFloat(0, defaultValue)
    array.recycle()
    return floatValue
}

@TargetApi(Build.VERSION_CODES.O)
fun Context.resolveFontAttr(attr: Int, defaultValue: Typeface? = null): Typeface? {
    val array = getTypedArrayWithAttributes(attr)
    val font = array.getFont(0)
    array.recycle()
    return font ?: defaultValue
}

fun Context.resolveIntAttr(attr: Int, defaultValue: Int = 0): Int {
    val array = getTypedArrayWithAttributes(attr)
    val intValue = array.getInt(0, defaultValue)
    array.recycle()
    return intValue
}

fun Context.resolveIntegerAttr(attr: Int, defaultValue: Int? = 0): Int {
    val array = getTypedArrayWithAttributes(attr)
    val integer = array.getInteger(0, defaultValue!!)
    array.recycle()
    return integer
}

fun Context.resolveStringAttr(attr: Int, defaultValue: String? = null): String? {
    val array = getTypedArrayWithAttributes(attr)
    val string = array.getString(0)
    array.recycle()
    return string ?: defaultValue
}

fun Context.resolveTextAttr(attr: Int, defaultValue: CharSequence? = null): CharSequence? {
    val array = getTypedArrayWithAttributes(attr)
    val charSequence = array.getText(0)
    array.recycle()
    return charSequence ?: defaultValue
}

fun Context.resolveTextArrayAttr(attr: Int,
                                 defaultValue: Array<CharSequence>? = null): Array<CharSequence>? {
    val array = getTypedArrayWithAttributes(attr)
    val charSequence = array.getTextArray(0)
    array.recycle()
    return charSequence ?: defaultValue
}

fun Context.getTypedArrayWithAttributes(vararg attr: Int): TypedArray =
    theme.obtainStyledAttributes(attr)

fun Fragment.resolveBooleanAttr(attr: Int, defaultValue: Boolean = false) =
    requireActivity().resolveBooleanAttr(attr, defaultValue)

fun Fragment.resolveColorAttr(attr: Int, defaultValue: Int = 0) =
    requireActivity().resolveColorAttr(attr, defaultValue)

fun Fragment.resolveColorStateListAttr(attr: Int,
                                      defaultValue: ColorStateList? = null) =
    requireActivity().resolveColorStateListAttr(attr, defaultValue)

fun Fragment.resolveDimenAttr(attr: Int, defaultValue: Float = 0f) =
    requireActivity().resolveDimenAttr(attr, defaultValue)

fun Fragment.resolveDimenPxOffsetAttr(attr: Int, defaultValue: Int = 0) =
    requireActivity().resolveDimenPxOffsetAttr(attr, defaultValue)

fun Fragment.resolveDimenPxAttr(attr: Int, defaultValue: Int = 0) =
    requireActivity().resolveDimenPxAttr(attr, defaultValue)

fun Fragment.resolveDrawableAttr(attr: Int, defaultValue: Drawable? = null) =
    requireActivity().resolveDrawableAttr(attr, defaultValue)

fun Fragment.resolveFloatAttr(attr: Int, defaultValue: Float = 0f) =
    requireActivity().resolveFloatAttr(attr, defaultValue)

@TargetApi(Build.VERSION_CODES.O)
fun Fragment.resolveFontAttr(attr: Int, defaultValue: Typeface? = null) =
    requireActivity().resolveFontAttr(attr, defaultValue)

fun Fragment.resolveIntAttr(attr: Int, defaultValue: Int = 0) =
    requireActivity().resolveIntAttr(attr, defaultValue)

fun Fragment.resolveIntegerAttr(attr: Int, defaultValue: Int? = 0) =
    requireActivity().resolveIntegerAttr(attr, defaultValue)

fun Fragment.resolveStringAttr(attr: Int, defaultValue: String? = null) =
    requireActivity().resolveStringAttr(attr, defaultValue)

fun Fragment.resolveTextAttr(attr: Int, defaultValue: CharSequence? = null) =
    requireActivity().resolveTextAttr(attr, defaultValue)

fun Fragment.resolveTextArrayAttr(attr: Int,
                                 defaultValue: Array<CharSequence>? = null) =
    requireActivity().resolveTextArrayAttr(attr, defaultValue)

fun View.resolveBooleanAttr(attr: Int, defaultValue: Boolean = false) =
    context.resolveBooleanAttr(attr, defaultValue)

fun View.resolveColorAttr(attr: Int, defaultValue: Int = 0) =
    context.resolveColorAttr(attr, defaultValue)

fun View.resolveColorStateListAttr(attr: Int,
                                       defaultValue: ColorStateList? = null) =
    context.resolveColorStateListAttr(attr, defaultValue)

fun View.resolveDimenAttr(attr: Int, defaultValue: Float = 0f) =
    context.resolveDimenAttr(attr, defaultValue)

fun View.resolveDimenPxOffsetAttr(attr: Int, defaultValue: Int = 0) =
    context.resolveDimenPxOffsetAttr(attr, defaultValue)

fun View.resolveDimenPxAttr(attr: Int, defaultValue: Int = 0) =
    context.resolveDimenPxAttr(attr, defaultValue)

fun View.resolveDrawableAttr(attr: Int, defaultValue: Drawable? = null) =
    context.resolveDrawableAttr(attr, defaultValue)

fun View.resolveFloatAttr(attr: Int, defaultValue: Float = 0f) =
    context.resolveFloatAttr(attr, defaultValue)

@TargetApi(Build.VERSION_CODES.O)
fun View.resolveFontAttr(attr: Int, defaultValue: Typeface? = null) =
    context.resolveFontAttr(attr, defaultValue)

fun View.resolveIntAttr(attr: Int, defaultValue: Int = 0) =
    context.resolveIntAttr(attr, defaultValue)

fun View.resolveIntegerAttr(attr: Int, defaultValue: Int? = 0) =
    context.resolveIntegerAttr(attr, defaultValue)

fun View.resolveStringAttr(attr: Int, defaultValue: String? = null) =
    context.resolveStringAttr(attr, defaultValue)

fun View.resolveTextAttr(attr: Int, defaultValue: CharSequence? = null) =
    context.resolveTextAttr(attr, defaultValue)

fun View.resolveTextArrayAttr(attr: Int,
                                  defaultValue: Array<CharSequence>? = null) =
    context.resolveTextArrayAttr(attr, defaultValue)