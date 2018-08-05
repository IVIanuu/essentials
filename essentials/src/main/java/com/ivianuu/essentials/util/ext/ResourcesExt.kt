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

// Aliases to other public API.

package com.ivianuu.essentials.util.ext

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.util.TypedValue
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.ivianuu.essentials.util.ContextAware

inline fun Context.anim(resId: Int): Animation = AnimationUtils.loadAnimation(this, resId)

inline fun Context.intArray(resId: Int): IntArray = resources.getIntArray(resId)

inline fun Context.stringArray(resId: Int): Array<String> =
    resources.getStringArray(resId)

inline fun Context.textArray(resId: Int): Array<CharSequence> =
    resources.getTextArray(resId)

inline fun Context.getResTypedArray(resId: Int): TypedArray = resources.obtainTypedArray(resId)

inline fun Context.bool(resId: Int): Boolean = resources.getBoolean(resId)

inline fun Context.dimen(resId: Int): Float = resources.getDimension(resId)

inline fun Context.dimenPx(resId: Int): Int = resources.getDimensionPixelSize(resId)

inline fun Context.dimenPxOffset(resId: Int): Int = resources.getDimensionPixelOffset(resId)

inline fun Context.float(resId: Int): Float {
    val value = VALUE
    resources.getValue(resId, value, true)
    return value.float
}

inline fun Context.int(resId: Int): Int = resources.getInteger(resId)

inline fun Context.bitmap(resId: Int): Bitmap = BitmapFactory.decodeResource(resources, resId)

inline fun Context.color(resId: Int): Int = ContextCompat.getColor(this, resId)

inline fun Context.colorStateList(resId: Int): ColorStateList =
    ContextCompat.getColorStateList(this, resId)!!

inline fun Context.drawable(resId: Int): Drawable =
    ContextCompat.getDrawable(this, resId)!!

inline fun Context.font(resId: Int): Typeface = ResourcesCompat.getFont(this, resId)!!

inline fun ContextAware.anim(resId: Int) = providedContext.anim(resId)

inline fun ContextAware.intArray(resId: Int) = providedContext.intArray(resId)

inline fun ContextAware.stringArray(resId: Int) = providedContext.stringArray(resId)

inline fun ContextAware.textArray(resId: Int) = providedContext.textArray(resId)

inline fun ContextAware.getResTypedArray(resId: Int) = providedContext.getResTypedArray(resId)

inline fun ContextAware.bool(resId: Int) = providedContext.bool(resId)

inline fun ContextAware.dimen(resId: Int) = providedContext.dimen(resId)

inline fun ContextAware.dimenPx(resId: Int) = providedContext.dimenPx(resId)

inline fun ContextAware.dimenPxOffset(resId: Int) = providedContext.dimenPxOffset(resId)

inline fun ContextAware.float(resId: Int) = providedContext.float(resId)

inline fun ContextAware.int(resId: Int): Int = providedContext.int(resId)

inline fun ContextAware.bitmap(resId: Int) = providedContext.bitmap(resId)

inline fun ContextAware.color(resId: Int) = providedContext.color(resId)

inline fun ContextAware.colorStateList(resId: Int) =
    providedContext.colorStateList(resId)

inline fun ContextAware.drawable(resId: Int) = providedContext.drawable(resId)

inline fun ContextAware.font(resId: Int) = providedContext.font(resId)

@PublishedApi
internal val VALUE = TypedValue()
