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

@file:Suppress("NOTHING_TO_INLINE")

package com.ivianuu.essentials.util.ext

import android.annotation.TargetApi
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.content.res.use
import com.ivianuu.essentials.util.ContextAware

inline fun Context.booleanAttr(attr: Int, defaultValue: Boolean = false): Boolean =
    withTypedArray(attr) { it.getBoolean(0, defaultValue) }

inline fun Context.colorAttr(attr: Int, defaultValue: Int = 0): Int =
    withTypedArray(attr) { it.getColor(0, defaultValue) }

inline fun Context.colorStateListAttr(
    attr: Int,
    defaultValue: ColorStateList? = null
): ColorStateList? = withTypedArray(attr) { it.getColorStateList(0) ?: defaultValue }

inline fun Context.dimenAttr(attr: Int, defaultValue: Float = 0f): Float =
    withTypedArray(attr) { it.getDimension(0, defaultValue) }

inline fun Context.dimenPxOffsetAttr(attr: Int, defaultValue: Int = 0): Int =
    withTypedArray(attr) { it.getDimensionPixelOffset(0, defaultValue) }

inline fun Context.dimenPxAttr(attr: Int, defaultValue: Int = 0): Int =
    withTypedArray(attr) { it.getDimensionPixelSize(0, defaultValue) }

inline fun Context.drawableAttr(attr: Int, defaultValue: Drawable? = null): Drawable? =
    withTypedArray(attr) { it.getDrawable(0) ?: defaultValue }

inline fun Context.floatAttr(attr: Int, defaultValue: Float = 0f): Float =
    withTypedArray(attr) { it.getFloat(0, defaultValue) }

@TargetApi(Build.VERSION_CODES.O)
inline fun Context.fontAttr(attr: Int, defaultValue: Typeface? = null): Typeface? =
    withTypedArray(attr) { it.getFont(0) ?: defaultValue }

inline fun Context.intArrayAttr(attr: Int, defaultValue: Int = 0): Int =
    withTypedArray(attr) { it.getInt(0, defaultValue) }

inline fun Context.integerAttr(attr: Int, defaultValue: Int = 0): Int =
    withTypedArray(attr) { it.getInteger(0, defaultValue) }

inline fun Context.stringAttr(attr: Int, defaultValue: String? = null): String? =
    withTypedArray(attr) { it.getString(0) ?: defaultValue }

inline fun Context.textAttr(attr: Int, defaultValue: CharSequence? = null): CharSequence? =
    withTypedArray(attr) { it.getText(0) ?: defaultValue }

inline fun Context.textArrayAttr(
    attr: Int,
    defaultValue: Array<CharSequence>? = null
): Array<CharSequence>? = withTypedArray(attr) { it.getTextArray(0) ?: defaultValue }

inline fun ContextAware.booleanAttr(attr: Int, defaultValue: Boolean = false) =
    providedContext.booleanAttr(attr, defaultValue)

inline fun ContextAware.colorAttr(attr: Int, defaultValue: Int = 0) =
    providedContext.colorAttr(attr, defaultValue)

inline fun ContextAware.colorStateListAttr(
    attr: Int,
    defaultValue: ColorStateList? = null
) =
    providedContext.colorStateListAttr(attr, defaultValue)

inline fun ContextAware.dimenAttr(attr: Int, defaultValue: Float = 0f) =
    providedContext.dimenAttr(attr, defaultValue)

inline fun ContextAware.dimenPxOffsetAttr(attr: Int, defaultValue: Int = 0) =
    providedContext.dimenPxOffsetAttr(attr, defaultValue)

inline fun ContextAware.dimenPxAttr(attr: Int, defaultValue: Int = 0) =
    providedContext.dimenPxAttr(attr, defaultValue)

inline fun ContextAware.drawableAttr(attr: Int, defaultValue: Drawable? = null) =
    providedContext.drawableAttr(attr, defaultValue)

inline fun ContextAware.floatAttr(attr: Int, defaultValue: Float = 0f) =
    providedContext.floatAttr(attr, defaultValue)

@TargetApi(Build.VERSION_CODES.O)
inline fun ContextAware.fontAttr(attr: Int, defaultValue: Typeface? = null) =
    providedContext.fontAttr(attr, defaultValue)

inline fun ContextAware.intArrayAttr(attr: Int, defaultValue: Int = 0) =
    providedContext.intArrayAttr(attr, defaultValue)

inline fun ContextAware.integerAttr(attr: Int, defaultValue: Int = 0) =
    providedContext.integerAttr(attr, defaultValue)

inline fun ContextAware.stringAttr(attr: Int, defaultValue: String? = null) =
    providedContext.stringAttr(attr, defaultValue)

inline fun ContextAware.textAttr(attr: Int, defaultValue: CharSequence? = null) =
    providedContext.textAttr(attr, defaultValue)

inline fun ContextAware.textArrayAttr(
    attr: Int,
    defaultValue: Array<CharSequence>? = null
) =
    providedContext.textArrayAttr(attr, defaultValue)

@PublishedApi
internal inline fun <T> Context.withTypedArray(
    vararg attr: Int,
    block: (TypedArray) -> T
): T =
    theme.obtainStyledAttributes(attr).use(block)