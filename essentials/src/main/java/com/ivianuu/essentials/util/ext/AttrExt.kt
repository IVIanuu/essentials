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

@file:Suppress("NOTHING_TO_INLINE") // Aliases to other public API.

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

inline fun Context.resolveBooleanAttr(attr: Int, defaultValue: Boolean = false): Boolean {
    return withTypedArray(attr) { it.getBoolean(0, defaultValue) }
}

inline fun Context.resolveColorAttr(attr: Int, defaultValue: Int = 0): Int {
    return withTypedArray(attr) { it.getColor(0, defaultValue) }
}

inline fun Context.resolveColorStateListAttr(
    attr: Int,
    defaultValue: ColorStateList? = null
): ColorStateList? {
    return withTypedArray(attr) { it.getColorStateList(0) ?: defaultValue }
}

inline fun Context.resolveDimenAttr(attr: Int, defaultValue: Float = 0f): Float {
    return withTypedArray(attr) { it.getDimension(0, defaultValue) }
}

inline fun Context.resolveDimenPxOffsetAttr(attr: Int, defaultValue: Int = 0): Int {
    return withTypedArray(attr) { it.getDimensionPixelOffset(0, defaultValue) }
}

inline fun Context.resolveDimenPxAttr(attr: Int, defaultValue: Int = 0): Int {
    return withTypedArray(attr) { it.getDimensionPixelSize(0, defaultValue) }
}

inline fun Context.resolveDrawableAttr(attr: Int, defaultValue: Drawable? = null): Drawable? {
    return withTypedArray(attr) { it.getDrawable(0) ?: defaultValue }
}

inline fun Context.resolveFloatAttr(attr: Int, defaultValue: Float = 0f): Float {
    return withTypedArray(attr) { it.getFloat(0, defaultValue) }
}

@TargetApi(Build.VERSION_CODES.O)
inline fun Context.resolveFontAttr(attr: Int, defaultValue: Typeface? = null): Typeface? {
    return withTypedArray(attr) { it.getFont(0) ?: defaultValue }
}

inline fun Context.resolveIntAttr(attr: Int, defaultValue: Int = 0): Int {
    return withTypedArray(attr) { it.getInt(0, defaultValue) }
}

inline fun Context.resolveIntegerAttr(attr: Int, defaultValue: Int = 0): Int {
    return withTypedArray(attr) { it.getInteger(0, defaultValue) }
}

inline fun Context.resolveStringAttr(attr: Int, defaultValue: String? = null): String? {
    return withTypedArray(attr) { it.getString(0) ?: defaultValue }
}

inline fun Context.resolveTextAttr(attr: Int, defaultValue: CharSequence? = null): CharSequence? {
    return withTypedArray(attr) { it.getText(0) ?: defaultValue }
}

inline fun Context.resolveTextArrayAttr(
    attr: Int,
    defaultValue: Array<CharSequence>? = null
): Array<CharSequence>? {
    return withTypedArray(attr) { it.getTextArray(0) ?: defaultValue }
}

inline fun ContextAware.resolveBooleanAttr(attr: Int, defaultValue: Boolean = false) =
    providedContext.resolveBooleanAttr(attr, defaultValue)

inline fun ContextAware.resolveColorAttr(attr: Int, defaultValue: Int = 0) =
    providedContext.resolveColorAttr(attr, defaultValue)

inline fun ContextAware.resolveColorStateListAttr(
    attr: Int,
    defaultValue: ColorStateList? = null
) =
    providedContext.resolveColorStateListAttr(attr, defaultValue)

inline fun ContextAware.resolveDimenAttr(attr: Int, defaultValue: Float = 0f) =
    providedContext.resolveDimenAttr(attr, defaultValue)

inline fun ContextAware.resolveDimenPxOffsetAttr(attr: Int, defaultValue: Int = 0) =
    providedContext.resolveDimenPxOffsetAttr(attr, defaultValue)

inline fun ContextAware.resolveDimenPxAttr(attr: Int, defaultValue: Int = 0) =
    providedContext.resolveDimenPxAttr(attr, defaultValue)

inline fun ContextAware.resolveDrawableAttr(attr: Int, defaultValue: Drawable? = null) =
    providedContext.resolveDrawableAttr(attr, defaultValue)

inline fun ContextAware.resolveFloatAttr(attr: Int, defaultValue: Float = 0f) =
    providedContext.resolveFloatAttr(attr, defaultValue)

@TargetApi(Build.VERSION_CODES.O)
inline fun ContextAware.resolveFontAttr(attr: Int, defaultValue: Typeface? = null) =
    providedContext.resolveFontAttr(attr, defaultValue)

inline fun ContextAware.resolveIntAttr(attr: Int, defaultValue: Int = 0) =
    providedContext.resolveIntAttr(attr, defaultValue)

inline fun ContextAware.resolveIntegerAttr(attr: Int, defaultValue: Int = 0) =
    providedContext.resolveIntegerAttr(attr, defaultValue)

inline fun ContextAware.resolveStringAttr(attr: Int, defaultValue: String? = null) =
    providedContext.resolveStringAttr(attr, defaultValue)

inline fun ContextAware.resolveTextAttr(attr: Int, defaultValue: CharSequence? = null) =
    providedContext.resolveTextAttr(attr, defaultValue)

inline fun ContextAware.resolveTextArrayAttr(
    attr: Int,
    defaultValue: Array<CharSequence>? = null
) =
    providedContext.resolveTextArrayAttr(attr, defaultValue)

@PublishedApi
internal inline fun <T> Context.withTypedArray(vararg attr: Int,
                                               action: (TypedArray) -> T): T =
    theme.obtainStyledAttributes(attr).use(action)

@PublishedApi
internal inline fun Context.getTypedArrayWithAttributes(vararg attr: Int): TypedArray =
    theme.obtainStyledAttributes(attr)