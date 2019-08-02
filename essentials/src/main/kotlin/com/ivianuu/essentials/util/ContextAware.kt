/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.util

import android.annotation.TargetApi
import android.app.Activity
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.View
import android.view.animation.Animation
import androidx.annotation.RequiresApi
import com.ivianuu.kommon.core.content.anim
import com.ivianuu.kommon.core.content.bitmap
import com.ivianuu.kommon.core.content.bool
import com.ivianuu.kommon.core.content.booleanAttr
import com.ivianuu.kommon.core.content.color
import com.ivianuu.kommon.core.content.colorAttr
import com.ivianuu.kommon.core.content.colorStateList
import com.ivianuu.kommon.core.content.colorStateListAttr
import com.ivianuu.kommon.core.content.componentName
import com.ivianuu.kommon.core.content.dimen
import com.ivianuu.kommon.core.content.dimenAttr
import com.ivianuu.kommon.core.content.dimenPx
import com.ivianuu.kommon.core.content.dimenPxAttr
import com.ivianuu.kommon.core.content.dimenPxOffset
import com.ivianuu.kommon.core.content.dimenPxOffsetAttr
import com.ivianuu.kommon.core.content.dp
import com.ivianuu.kommon.core.content.drawable
import com.ivianuu.kommon.core.content.drawableAttr
import com.ivianuu.kommon.core.content.float
import com.ivianuu.kommon.core.content.floatAttr
import com.ivianuu.kommon.core.content.font
import com.ivianuu.kommon.core.content.fontAttr
import com.ivianuu.kommon.core.content.hasPermissions
import com.ivianuu.kommon.core.content.int
import com.ivianuu.kommon.core.content.intArray
import com.ivianuu.kommon.core.content.intArrayAttr
import com.ivianuu.kommon.core.content.integerAttr
import com.ivianuu.kommon.core.content.intent
import com.ivianuu.kommon.core.content.registerReceiver
import com.ivianuu.kommon.core.content.startActivity
import com.ivianuu.kommon.core.content.startForegroundService
import com.ivianuu.kommon.core.content.startForegroundServiceCompat
import com.ivianuu.kommon.core.content.startService
import com.ivianuu.kommon.core.content.string
import com.ivianuu.kommon.core.content.stringArray
import com.ivianuu.kommon.core.content.stringAttr
import com.ivianuu.kommon.core.content.systemService
import com.ivianuu.kommon.core.content.systemServiceOrNull
import com.ivianuu.kommon.core.content.textArray
import com.ivianuu.kommon.core.content.textArrayAttr
import com.ivianuu.kommon.core.content.textAttr
import com.ivianuu.kommon.core.content.typedArray

// todo remove

/**
 * Marks a component as context aware
 */
interface ContextAware {
    val providedContext: Context
}

fun ContextAware.booleanAttr(attr: Int, defaultValue: Boolean = false): Boolean =
    providedContext.booleanAttr(attr, defaultValue)

fun ContextAware.colorAttr(attr: Int, defaultValue: Int = 0): Int =
    providedContext.colorAttr(attr, defaultValue)

fun ContextAware.colorStateListAttr(
    attr: Int,
    defaultValue: ColorStateList? = null
): ColorStateList? =
    providedContext.colorStateListAttr(attr, defaultValue)

fun ContextAware.dimenAttr(attr: Int, defaultValue: Float = 0f): Float =
    providedContext.dimenAttr(attr, defaultValue)

fun ContextAware.dimenPxOffsetAttr(attr: Int, defaultValue: Int = 0): Int =
    providedContext.dimenPxOffsetAttr(attr, defaultValue)

fun ContextAware.dimenPxAttr(attr: Int, defaultValue: Int = 0): Int =
    providedContext.dimenPxAttr(attr, defaultValue)

fun ContextAware.drawableAttr(attr: Int, defaultValue: Drawable? = null): Drawable? =
    providedContext.drawableAttr(attr, defaultValue)

fun ContextAware.floatAttr(attr: Int, defaultValue: Float = 0f): Float =
    providedContext.floatAttr(attr, defaultValue)

@TargetApi(26)
fun ContextAware.fontAttr(attr: Int, defaultValue: Typeface? = null): Typeface? =
    providedContext.fontAttr(attr, defaultValue)

fun ContextAware.intArrayAttr(attr: Int, defaultValue: Int = 0): Int =
    providedContext.intArrayAttr(attr, defaultValue)

fun ContextAware.integerAttr(attr: Int, defaultValue: Int = 0): Int =
    providedContext.integerAttr(attr, defaultValue)

fun ContextAware.stringAttr(attr: Int, defaultValue: String? = null): String? =
    providedContext.stringAttr(attr, defaultValue)

fun ContextAware.textAttr(attr: Int, defaultValue: CharSequence? = null): CharSequence? =
    providedContext.textAttr(attr, defaultValue)

fun ContextAware.textArrayAttr(
    attr: Int,
    defaultValue: Array<CharSequence>? = null
): Array<CharSequence>? = providedContext.textArrayAttr(attr, defaultValue)

fun ContextAware.anim(resId: Int): Animation = providedContext.anim(resId)

fun ContextAware.intArray(resId: Int): IntArray = providedContext.intArray(resId)

fun ContextAware.stringArray(resId: Int): Array<String> = providedContext.stringArray(resId)

fun ContextAware.textArray(resId: Int): Array<CharSequence> = providedContext.textArray(resId)

fun ContextAware.typedArray(resId: Int): TypedArray = providedContext.typedArray(resId)

fun ContextAware.bool(resId: Int): Boolean = providedContext.bool(resId)

fun ContextAware.dimen(resId: Int): Float = providedContext.dimen(resId)

fun ContextAware.dimenPx(resId: Int): Int = providedContext.dimenPx(resId)

fun ContextAware.dimenPxOffset(resId: Int): Int = providedContext.dimenPxOffset(resId)

fun ContextAware.float(resId: Int): Float = providedContext.float(resId)

fun ContextAware.int(resId: Int): Int = providedContext.int(resId)

fun ContextAware.bitmap(resId: Int): Bitmap = providedContext.bitmap(resId)

fun ContextAware.color(resId: Int): Int = providedContext.color(resId)

fun ContextAware.colorStateList(resId: Int): ColorStateList =
    providedContext.colorStateList(resId)

fun ContextAware.drawable(resId: Int): Drawable = providedContext.drawable(resId)

fun ContextAware.font(resId: Int): Typeface = providedContext.font(resId)

fun ContextAware.string(resId: Int): String = providedContext.string(resId)

fun ContextAware.string(resId: Int, vararg args: Any): String =
    providedContext.string(resId, *args)

inline fun <reified T> ContextAware.intent(): Intent = providedContext.intent<T>()

inline fun <reified T> ContextAware.intent(init: Intent.() -> Unit): Intent =
    providedContext.intent<T>(init)

inline fun <reified T> View.intent(init: Intent.() -> Unit): Intent =
    context.intent<T>(init)

inline fun <reified T : Activity> ContextAware.startActivity() {
    providedContext.startActivity<T>()
}

inline fun <reified T : Activity> ContextAware.startActivity(init: Intent.() -> Unit) {
    providedContext.startActivity<T>(init)
}

inline fun <reified T : Service> ContextAware.startService() {
    providedContext.startService<T>()
}

inline fun <reified T : Service> ContextAware.startService(init: Intent.() -> Unit) {
    providedContext.startService<T>(init)
}

@RequiresApi(26)
inline fun <reified T : Service> ContextAware.startForegroundService() {
    providedContext.startForegroundService<T>()
}

@RequiresApi(26)
inline fun <reified T : Service> ContextAware.startForegroundService(init: Intent.() -> Unit) {
    providedContext.startForegroundService<T>(init)
}

inline fun <reified T : Service> ContextAware.startForegroundServiceCompat() {
    providedContext.startForegroundServiceCompat<T>()
}

inline fun <reified T : Service> ContextAware.startForegroundServiceCompat(init: Intent.() -> Unit) {
    providedContext.startForegroundServiceCompat<T>(init)
}

fun ContextAware.dp(dp: Int): Float = providedContext.dp(dp)

inline fun <reified T> ContextAware.componentName(): ComponentName =
    providedContext.componentName<T>()

fun ContextAware.componentName(className: String): ComponentName =
    providedContext.componentName(className)

fun ContextAware.startForegroundServiceCompat(intent: Intent) {
    providedContext.startForegroundServiceCompat(intent)
}

fun ContextAware.registerReceiver(
    intentFilter: IntentFilter,
    onReceive: (intent: Intent) -> Unit
): BroadcastReceiver = providedContext.registerReceiver(intentFilter, onReceive)

fun ContextAware.hasPermissions(vararg permissions: String): Boolean =
    providedContext.hasPermissions(*permissions)

inline fun <reified T> ContextAware.systemService(): T =
    providedContext.systemService()

inline fun <reified T> ContextAware.systemServiceOrNull(): T? =
    providedContext.systemServiceOrNull<T>()