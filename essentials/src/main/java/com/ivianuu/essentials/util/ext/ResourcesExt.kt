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

inline fun Context.getResAnim(resId: Int): Animation = AnimationUtils.loadAnimation(this, resId)

inline fun Context.getResIntArray(resId: Int): IntArray = resources.getIntArray(resId)

inline fun Context.getResStringArray(resId: Int): Array<String> =
    resources.getStringArray(resId)

inline fun Context.getResTextArray(resId: Int): Array<CharSequence> =
    resources.getTextArray(resId)

inline fun Context.getResTypedArray(resId: Int): TypedArray = resources.obtainTypedArray(resId)

inline fun Context.getResBool(resId: Int): Boolean = resources.getBoolean(resId)

inline fun Context.getResDimen(resId: Int): Float = resources.getDimension(resId)

inline fun Context.getResDimenPx(resId: Int): Int = resources.getDimensionPixelSize(resId)

inline fun Context.getResDimenPxOffset(resId: Int): Int = resources.getDimensionPixelOffset(resId)

inline fun Context.getResFloat(resId: Int): Float {
    val value = VALUE
    resources.getValue(resId, value, true)
    return value.float
}

inline fun Context.getResInt(resId: Int): Int = resources.getInteger(resId)

inline fun Context.getResBitmap(resId: Int): Bitmap = BitmapFactory.decodeResource(resources, resId)

inline fun Context.getResColor(resId: Int): Int = ContextCompat.getColor(this, resId)

inline fun Context.getResColorStateList(resId: Int): ColorStateList =
    ContextCompat.getColorStateList(this, resId)!!

inline fun Context.getResDrawable(resId: Int): Drawable =
    ContextCompat.getDrawable(this, resId)!!

inline fun Context.getResFont(resId: Int): Typeface = ResourcesCompat.getFont(this, resId)!!

inline fun ContextAware.getResAnim(resId: Int) = providedContext.getResAnim(resId)

inline fun ContextAware.getResIntArray(resId: Int) = providedContext.getResIntArray(resId)

inline fun ContextAware.getResStringArray(resId: Int) = providedContext.getResStringArray(resId)

inline fun ContextAware.getResTextArray(resId: Int) = providedContext.getResTextArray(resId)

inline fun ContextAware.getResTypedArray(resId: Int) = providedContext.getResTypedArray(resId)

inline fun ContextAware.getResBool(resId: Int) = providedContext.getResBool(resId)

inline fun ContextAware.getResDimen(resId: Int) = providedContext.getResDimen(resId)

inline fun ContextAware.getResDimenPx(resId: Int) = providedContext.getResDimenPx(resId)

inline fun ContextAware.getResDimenPxOffset(resId: Int) = providedContext.getResDimenPxOffset(resId)

inline fun ContextAware.getResFloat(resId: Int) = providedContext.getResFloat(resId)

inline fun ContextAware.getResInt(resId: Int): Int = providedContext.getResInt(resId)

inline fun ContextAware.getResBitmap(resId: Int) = providedContext.getResBitmap(resId)

inline fun ContextAware.getResColor(resId: Int) = providedContext.getResColor(resId)

inline fun ContextAware.getResColorStateList(resId: Int) =
    providedContext.getResColorStateList(resId)

inline fun ContextAware.getResDrawable(resId: Int) = providedContext.getResDrawable(resId)

inline fun ContextAware.getResFont(resId: Int) = providedContext.getResFont(resId)

@PublishedApi
internal val VALUE = TypedValue()
