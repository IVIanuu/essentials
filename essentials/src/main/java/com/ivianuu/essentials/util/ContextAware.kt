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
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import com.ivianuu.androidktx.core.content.anim
import com.ivianuu.androidktx.core.content.app
import com.ivianuu.androidktx.core.content.batteryLevel
import com.ivianuu.androidktx.core.content.bitmap
import com.ivianuu.androidktx.core.content.bool
import com.ivianuu.androidktx.core.content.booleanAttr
import com.ivianuu.androidktx.core.content.color
import com.ivianuu.androidktx.core.content.colorAttr
import com.ivianuu.androidktx.core.content.colorStateList
import com.ivianuu.androidktx.core.content.colorStateListAttr
import com.ivianuu.androidktx.core.content.componentName
import com.ivianuu.androidktx.core.content.configuration
import com.ivianuu.androidktx.core.content.dimen
import com.ivianuu.androidktx.core.content.dimenAttr
import com.ivianuu.androidktx.core.content.dimenPx
import com.ivianuu.androidktx.core.content.dimenPxAttr
import com.ivianuu.androidktx.core.content.dimenPxOffset
import com.ivianuu.androidktx.core.content.dimenPxOffsetAttr
import com.ivianuu.androidktx.core.content.displayMetrics
import com.ivianuu.androidktx.core.content.dp
import com.ivianuu.androidktx.core.content.drawable
import com.ivianuu.androidktx.core.content.drawableAttr
import com.ivianuu.androidktx.core.content.float
import com.ivianuu.androidktx.core.content.floatAttr
import com.ivianuu.androidktx.core.content.font
import com.ivianuu.androidktx.core.content.fontAttr
import com.ivianuu.androidktx.core.content.hasPermissions
import com.ivianuu.androidktx.core.content.int
import com.ivianuu.androidktx.core.content.intArray
import com.ivianuu.androidktx.core.content.intArrayAttr
import com.ivianuu.androidktx.core.content.integerAttr
import com.ivianuu.androidktx.core.content.intent
import com.ivianuu.androidktx.core.content.isAppEnabled
import com.ivianuu.androidktx.core.content.isAppInstalled
import com.ivianuu.androidktx.core.content.isAppLaunchable
import com.ivianuu.androidktx.core.content.isCharging
import com.ivianuu.androidktx.core.content.isLandscape
import com.ivianuu.androidktx.core.content.isPortrait
import com.ivianuu.androidktx.core.content.isScreenOff
import com.ivianuu.androidktx.core.content.isScreenOn
import com.ivianuu.androidktx.core.content.realScreenHeight
import com.ivianuu.androidktx.core.content.realScreenWidth
import com.ivianuu.androidktx.core.content.registerReceiver
import com.ivianuu.androidktx.core.content.rotation
import com.ivianuu.androidktx.core.content.screenHeight
import com.ivianuu.androidktx.core.content.screenWidth
import com.ivianuu.androidktx.core.content.startActivity
import com.ivianuu.androidktx.core.content.startForegroundService
import com.ivianuu.androidktx.core.content.startForegroundServiceCompat
import com.ivianuu.androidktx.core.content.startService
import com.ivianuu.androidktx.core.content.string
import com.ivianuu.androidktx.core.content.stringArray
import com.ivianuu.androidktx.core.content.stringAttr
import com.ivianuu.androidktx.core.content.systemService
import com.ivianuu.androidktx.core.content.systemServiceOrNull
import com.ivianuu.androidktx.core.content.textArray
import com.ivianuu.androidktx.core.content.textArrayAttr
import com.ivianuu.androidktx.core.content.textAttr
import com.ivianuu.androidktx.core.content.typedArray

/**
 * Marks a component as context aware
 */
interface ContextAware {
    val providedContext: Context
}

fun ContextAware.booleanAttr(attr: Int, defaultValue: Boolean = false) =
    providedContext.booleanAttr(attr, defaultValue)

fun ContextAware.colorAttr(attr: Int, defaultValue: Int = 0) =
    providedContext.colorAttr(attr, defaultValue)

fun ContextAware.colorStateListAttr(
    attr: Int,
    defaultValue: ColorStateList? = null
) =
    providedContext.colorStateListAttr(attr, defaultValue)

fun ContextAware.dimenAttr(attr: Int, defaultValue: Float = 0f) =
    providedContext.dimenAttr(attr, defaultValue)

fun ContextAware.dimenPxOffsetAttr(attr: Int, defaultValue: Int = 0) =
    providedContext.dimenPxOffsetAttr(attr, defaultValue)

fun ContextAware.dimenPxAttr(attr: Int, defaultValue: Int = 0) =
    providedContext.dimenPxAttr(attr, defaultValue)

fun ContextAware.drawableAttr(attr: Int, defaultValue: Drawable? = null) =
    providedContext.drawableAttr(attr, defaultValue)

fun ContextAware.floatAttr(attr: Int, defaultValue: Float = 0f) =
    providedContext.floatAttr(attr, defaultValue)

@TargetApi(Build.VERSION_CODES.O)
fun ContextAware.fontAttr(attr: Int, defaultValue: Typeface? = null) =
    providedContext.fontAttr(attr, defaultValue)

fun ContextAware.intArrayAttr(attr: Int, defaultValue: Int = 0) =
    providedContext.intArrayAttr(attr, defaultValue)

fun ContextAware.integerAttr(attr: Int, defaultValue: Int = 0) =
    providedContext.integerAttr(attr, defaultValue)

fun ContextAware.stringAttr(attr: Int, defaultValue: String? = null) =
    providedContext.stringAttr(attr, defaultValue)

fun ContextAware.textAttr(attr: Int, defaultValue: CharSequence? = null) =
    providedContext.textAttr(attr, defaultValue)

fun ContextAware.textArrayAttr(
    attr: Int,
    defaultValue: Array<CharSequence>? = null
) = providedContext.textArrayAttr(attr, defaultValue)

fun ContextAware.anim(resId: Int) = providedContext.anim(resId)

fun ContextAware.intArray(resId: Int) = providedContext.intArray(resId)

fun ContextAware.stringArray(resId: Int) = providedContext.stringArray(resId)

fun ContextAware.textArray(resId: Int) = providedContext.textArray(resId)

fun ContextAware.typedArray(resId: Int) = providedContext.typedArray(resId)

fun ContextAware.bool(resId: Int) = providedContext.bool(resId)

fun ContextAware.dimen(resId: Int) = providedContext.dimen(resId)

fun ContextAware.dimenPx(resId: Int) = providedContext.dimenPx(resId)

fun ContextAware.dimenPxOffset(resId: Int) = providedContext.dimenPxOffset(resId)

fun ContextAware.float(resId: Int) = providedContext.float(resId)

fun ContextAware.int(resId: Int): Int = providedContext.int(resId)

fun ContextAware.bitmap(resId: Int) = providedContext.bitmap(resId)

fun ContextAware.color(resId: Int) = providedContext.color(resId)

fun ContextAware.colorStateList(resId: Int) =
    providedContext.colorStateList(resId)

fun ContextAware.drawable(resId: Int) = providedContext.drawable(resId)

fun ContextAware.font(resId: Int) = providedContext.font(resId)

fun ContextAware.string(resId: Int) = providedContext.string(resId)

fun ContextAware.string(resId: Int, vararg args: Any) =
    providedContext.string(resId, *args)

inline fun <reified T> ContextAware.intent() = providedContext.intent<T>()

inline fun <reified T> ContextAware.intent(init: Intent.() -> Unit) =
    providedContext.intent<T>(init)

inline fun <reified T> View.intent(init: Intent.() -> Unit) =
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

inline val ContextAware.configuration
    get() = providedContext.configuration

inline val ContextAware.displayMetrics
    get() = providedContext.displayMetrics

inline val ContextAware.rotation
    get() = providedContext.rotation

inline val ContextAware.isPortrait
    get() = providedContext.isPortrait

inline val ContextAware.isLandscape
    get() = providedContext.isLandscape

inline val ContextAware.screenWidth
    get() = providedContext.screenWidth

inline val ContextAware.screenHeight
    get() = providedContext.screenHeight

inline val ContextAware.realScreenWidth
    get() = providedContext.realScreenWidth

inline val ContextAware.realScreenHeight
    get() = providedContext.realScreenHeight

inline val ContextAware.isScreenOn
    get() =
        providedContext.isScreenOn

inline val ContextAware.isScreenOff
    get() =
        providedContext.isScreenOff

inline val ContextAware.isCharging
    get() = providedContext.isCharging

inline val ContextAware.batteryLevel
    get() = providedContext.batteryLevel

fun ContextAware.dp(dp: Int) = providedContext.dp(dp)

inline fun <reified T> ContextAware.componentName() =
    providedContext.componentName<T>()

fun ContextAware.componentName(className: String) =
    providedContext.componentName(className)

fun ContextAware.startForegroundServiceCompat(intent: Intent) {
    providedContext.startForegroundServiceCompat(intent)
}

fun ContextAware.registerReceiver(
    intentFilter: IntentFilter,
    onReceive: (intent: Intent) -> Unit
) = providedContext.registerReceiver(intentFilter, onReceive)

fun ContextAware.hasPermissions(vararg permissions: String) =
    providedContext.hasPermissions(*permissions)

fun ContextAware.isAppInstalled(packageName: String) =
    providedContext.isAppInstalled(packageName)

fun ContextAware.isAppLaunchable(packageName: String) =
    providedContext.isAppLaunchable(packageName)

fun ContextAware.isAppEnabled(packageName: String) =
    providedContext.isAppEnabled(packageName)

inline fun <reified T : Application> ContextAware.app() = providedContext.app<T>()

inline fun <reified T> ContextAware.systemService() =
    providedContext.systemService<T>()

inline fun <reified T> ContextAware.systemServiceOrNull() =
    providedContext.systemServiceOrNull<T>()