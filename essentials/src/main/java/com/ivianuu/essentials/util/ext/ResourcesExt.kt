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

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.util.TypedValue
import android.view.animation.Animation
import android.view.animation.AnimationUtils

fun Context.getResAnim(resId: Int) : Animation = AnimationUtils.loadAnimation(this, resId)

fun Context.getResIntArray(resId: Int) : IntArray = resources.getIntArray(resId)

fun Context.getResStringArray(resId: Int) : Array<String> =
    resources.getStringArray(resId)

fun Context.getResTextArray(resId: Int) : Array<CharSequence> =
    resources.getTextArray(resId)

fun Context.getResTypedArray(resId: Int) : TypedArray = resources.obtainTypedArray(resId)

fun Context.getResBool(resId: Int) : Boolean = resources.getBoolean(resId)

fun Context.getResDimen(resId : Int) : Float = this.resources.getDimension(resId)

fun Context.getResDimenPx(resId : Int) : Int = this.resources.getDimensionPixelSize(resId)

fun Context.getResDimenPxOffset(resId : Int) : Int = this.resources.getDimensionPixelOffset(resId)

fun Context.getResFloat(resId: Int) : Float {
    val value = ValueHolder.VALUE
    resources.getValue(resId, value, true)
    return value.float
}

fun Context.getResInt(resId: Int) : Int = resources.getInteger(resId)

fun Context.getResBitmap(resId: Int) : Bitmap = BitmapFactory.decodeResource(resources, resId)

fun Context.getResColor(resId: Int) : Int = ContextCompat.getColor(this, resId)

fun Context.getResColorStateList(resId: Int) : ColorStateList =
    ContextCompat.getColorStateList(this, resId)!!

fun Context.getResDrawable(resId : Int) : Drawable =
    ContextCompat.getDrawable(this, resId)!!

fun Context.getResFont(resId: Int) : Typeface = ResourcesCompat.getFont(this, resId)!!

fun Fragment.getResAnim(resId: Int) = requireActivity().getResAnim(resId)

fun Fragment.getResIntArray(resId: Int)  = requireActivity().getResIntArray(resId)

fun Fragment.getResStringArray(resId: Int) = requireActivity().getResStringArray(resId)

fun Fragment.getResTextArray(resId: Int)= requireActivity().getResTextArray(resId)

fun Fragment.getResTypedArray(resId: Int) = requireActivity().getResTypedArray(resId)

fun Fragment.getResBool(resId: Int) = requireActivity().getResBool(resId)

fun Fragment.getResDimen(resId : Int) = requireActivity().getResDimen(resId)

fun Fragment.getResDimenPx(resId : Int) = requireActivity().getResDimenPx(resId)

fun Fragment.getResDimenPxOffset(resId : Int) = requireActivity().getResDimenPxOffset(resId)

fun Fragment.getResFloat(resId: Int) = requireActivity().getResFloat(resId)

fun Fragment.getResInt(resId: Int) : Int = resources.getInteger(resId)

fun Fragment.getResBitmap(resId: Int) = requireActivity().getResBitmap(resId)

fun Fragment.getResColor(resId: Int) = requireActivity().getResColor(resId)

fun Fragment.getResColorStateList(resId: Int)= requireActivity().getResColorStateList(resId)

fun Fragment.getResDrawable(resId : Int)= requireActivity().getResDrawable(resId)

fun Fragment.getResFont(resId: Int) = requireActivity().getResFont(resId)

private object ValueHolder {
    val VALUE = TypedValue()
}
