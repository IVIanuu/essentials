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

package com.ivianuu.essentials.util

import android.annotation.TargetApi
import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.Animation
import androidx.annotation.RequiresApi
import com.ivianuu.kommon.core.content.*

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

@TargetApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
inline fun <reified T : Service> ContextAware.startForegroundService() {
    providedContext.startForegroundService<T>()
}

@RequiresApi(Build.VERSION_CODES.O)
inline fun <reified T : Service> ContextAware.startForegroundService(init: Intent.() -> Unit) {
    providedContext.startForegroundService<T>(init)
}

inline fun <reified T : Service> ContextAware.startForegroundServiceCompat() {
    providedContext.startForegroundServiceCompat<T>()
}

inline fun <reified T : Service> ContextAware.startForegroundServiceCompat(init: Intent.() -> Unit) {
    providedContext.startForegroundServiceCompat<T>(init)
}

inline val ContextAware.configuration: Configuration
    get() = providedContext.configuration

inline val ContextAware.displayMetrics: DisplayMetrics
    get() = providedContext.displayMetrics

inline val ContextAware.rotation: Int
    get() = providedContext.rotation

inline val ContextAware.isPortrait: Boolean
    get() = providedContext.isPortrait

inline val ContextAware.isLandscape: Boolean
    get() = providedContext.isLandscape

inline val ContextAware.screenWidth: Int
    get() = providedContext.screenWidth

inline val ContextAware.screenHeight: Int
    get() = providedContext.screenHeight

inline val ContextAware.realScreenWidth: Int
    get() = providedContext.realScreenWidth

inline val ContextAware.realScreenHeight: Int
    get() = providedContext.realScreenHeight

inline val ContextAware.isScreenOn: Boolean
    get() =
        providedContext.isScreenOn

inline val ContextAware.isScreenOff: Boolean
    get() =
        providedContext.isScreenOff

inline val ContextAware.isCharging: Boolean
    get() = providedContext.isCharging

inline val ContextAware.batteryLevel: Int
    get() = providedContext.batteryLevel

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

fun ContextAware.isAppInstalled(packageName: String): Boolean =
    providedContext.isAppInstalled(packageName)

fun ContextAware.isAppLaunchable(packageName: String): Boolean =
    providedContext.isAppLaunchable(packageName)

fun ContextAware.isAppEnabled(packageName: String): Boolean =
    providedContext.isAppEnabled(packageName)

inline fun <reified T : Application> ContextAware.app(): T = providedContext.app<T>()

inline fun <reified T> ContextAware.systemService(): T =
    providedContext.systemService<T>()

inline fun <reified T> ContextAware.systemServiceOrNull(): T? =
    providedContext.systemServiceOrNull<T>()