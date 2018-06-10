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
import com.ivianuu.essentials.util.ContextAware

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

fun Context.resolveColorStateListAttr(
    attr: Int,
    defaultValue: ColorStateList? = null
): ColorStateList? {
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

fun Context.resolveTextArrayAttr(
    attr: Int,
    defaultValue: Array<CharSequence>? = null
): Array<CharSequence>? {
    val array = getTypedArrayWithAttributes(attr)
    val charSequence = array.getTextArray(0)
    array.recycle()
    return charSequence ?: defaultValue
}

fun Context.getTypedArrayWithAttributes(vararg attr: Int): TypedArray =
    theme.obtainStyledAttributes(attr)


fun ContextAware.resolveBooleanAttr(attr: Int, defaultValue: Boolean = false) =
    providedContext.resolveBooleanAttr(attr, defaultValue)

fun ContextAware.resolveColorAttr(attr: Int, defaultValue: Int = 0) =
    providedContext.resolveColorAttr(attr, defaultValue)

fun ContextAware.resolveColorStateListAttr(
    attr: Int,
    defaultValue: ColorStateList? = null
) =
    providedContext.resolveColorStateListAttr(attr, defaultValue)

fun ContextAware.resolveDimenAttr(attr: Int, defaultValue: Float = 0f) =
    providedContext.resolveDimenAttr(attr, defaultValue)

fun ContextAware.resolveDimenPxOffsetAttr(attr: Int, defaultValue: Int = 0) =
    providedContext.resolveDimenPxOffsetAttr(attr, defaultValue)

fun ContextAware.resolveDimenPxAttr(attr: Int, defaultValue: Int = 0) =
    providedContext.resolveDimenPxAttr(attr, defaultValue)

fun ContextAware.resolveDrawableAttr(attr: Int, defaultValue: Drawable? = null) =
    providedContext.resolveDrawableAttr(attr, defaultValue)

fun ContextAware.resolveFloatAttr(attr: Int, defaultValue: Float = 0f) =
    providedContext.resolveFloatAttr(attr, defaultValue)

@TargetApi(Build.VERSION_CODES.O)
fun ContextAware.resolveFontAttr(attr: Int, defaultValue: Typeface? = null) =
    providedContext.resolveFontAttr(attr, defaultValue)

fun ContextAware.resolveIntAttr(attr: Int, defaultValue: Int = 0) =
    providedContext.resolveIntAttr(attr, defaultValue)

fun ContextAware.resolveIntegerAttr(attr: Int, defaultValue: Int? = 0) =
    providedContext.resolveIntegerAttr(attr, defaultValue)

fun ContextAware.resolveStringAttr(attr: Int, defaultValue: String? = null) =
    providedContext.resolveStringAttr(attr, defaultValue)

fun ContextAware.resolveTextAttr(attr: Int, defaultValue: CharSequence? = null) =
    providedContext.resolveTextAttr(attr, defaultValue)

fun ContextAware.resolveTextArrayAttr(
    attr: Int,
    defaultValue: Array<CharSequence>? = null
) =
    providedContext.resolveTextArrayAttr(attr, defaultValue)