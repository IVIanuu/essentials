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

import android.app.Application
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.PowerManager
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.ivianuu.essentials.util.BatteryUtil
import com.ivianuu.essentials.util.ContextAware

@PublishedApi
internal val _displayMetrics = DisplayMetrics()

inline val Context.configuration: Configuration
    get() = resources.configuration

inline val ContextAware.configuration
    get() = providedContext.configuration

inline val Fragment.configuration
    get() = requireContext().configuration

inline val View.configuration
    get() = context.configuration

inline val Context.displayMetrics: DisplayMetrics
    get() = resources.displayMetrics

inline val ContextAware.displayMetrics
    get() = providedContext.displayMetrics

inline val Fragment.displayMetrics
    get() = requireContext().displayMetrics

inline val View.displayMetrics
    get() = context.displayMetrics

inline val Context.rotation
    get() = systemService<WindowManager>().defaultDisplay.rotation

inline val ContextAware.rotation
    get() = providedContext.rotation

inline val Fragment.rotation
    get() = requireContext().rotation

inline val View.rotation
    get() = context.rotation

inline val Context.isPortrait
    get() = configuration.isPortrait

inline val ContextAware.isPortrait
    get() = providedContext.isPortrait

inline val Fragment.isPortrait
    get() = requireContext().isPortrait

inline val View.isPortrait
    get() = context.isPortrait

inline val Context.isLandscape
    get() = configuration.isLandscape

inline val ContextAware.isLandscape
    get() = providedContext.isLandscape

inline val Fragment.isLandscape
    get() = requireContext().isLandscape

inline val View.isLandscape
    get() = context.isLandscape

inline val Context.screenWidth: Int
    get() = displayMetrics.widthPixels

inline val ContextAware.screenWidth
    get() = providedContext.screenWidth

inline val Fragment.screenWidth
    get() = requireContext().screenWidth

inline val View.screenWidth
    get() = context.screenWidth

inline val Context.screenHeight: Int
    get() = displayMetrics.heightPixels

inline val ContextAware.screenHeight
    get() = providedContext.screenHeight

inline val Fragment.screenHeight
    get() = requireContext().screenHeight

inline val View.screenHeight
    get() = context.screenHeight

inline val Context.realScreenWidth: Int
    get() {
        systemService<WindowManager>().defaultDisplay.getRealMetrics(_displayMetrics)
        return _displayMetrics.widthPixels
    }

inline val ContextAware.realScreenWidth
    get() = providedContext.realScreenWidth

inline val Fragment.realScreenWidth
    get() = requireContext().realScreenWidth

inline val View.realScreenWidth
    get() = context.realScreenWidth

inline val Context.realScreenHeight: Int
    get() {
        systemService<WindowManager>().defaultDisplay.getRealMetrics(_displayMetrics)
        return _displayMetrics.heightPixels
    }

inline val ContextAware.realScreenHeight
    get() = providedContext.realScreenHeight

inline val Fragment.realScreenHeight
    get() = requireContext().realScreenHeight

inline val View.realScreenHeight
    get() = context.realScreenHeight

inline val Context.isScreenOn
    get() =
        systemService<PowerManager>().isInteractive

inline val ContextAware.isScreenOn
    get() =
        providedContext.isScreenOn

inline val Fragment.isScreenOn
    get() =
        requireContext().isScreenOn

inline val View.isScreenOn
    get() =
        context.isScreenOn

inline val Context.isScreenOff
    get() =
        !systemService<PowerManager>().isInteractive

inline val ContextAware.isScreenOff
    get() =
        providedContext.isScreenOff

inline val Fragment.isScreenOff
    get() =
        requireContext().isScreenOff

inline val View.isScreenOff
    get() =
        context.isScreenOff

inline val Context.isCharging: Boolean
    get() = BatteryUtil.isCharging(this)

inline val ContextAware.isCharging
    get() = providedContext.isCharging

inline val Fragment.isCharging
    get() = requireContext().isCharging

inline val View.isCharging
    get() = context.isCharging

inline val Context.batteryLevel: Int
    get() = BatteryUtil.getBatteryLevel(this)

inline val ContextAware.batteryLevel
    get() = providedContext.batteryLevel

inline val Fragment.batteryLevel
    get() = requireContext().batteryLevel

inline val View.batteryLevel
    get() = context.batteryLevel

inline fun Context.dp(dp: Int) = dp * displayMetrics.density

inline fun ContextAware.dp(dp: Int) = providedContext.dp(dp)

inline fun Fragment.dp(dp: Int) = requireContext().dp(dp)

inline fun View.dp(dp: Int) = context.dp(dp)

inline fun <reified T> Context.componentName() = ComponentName(this, T::class.java)

inline fun <reified T> ContextAware.componentName() =
    providedContext.componentName<T>()

inline fun <reified T> Fragment.componentName() =
    requireContext().componentName<T>()

inline fun <reified T> View.componentName() =
    context.componentName<T>()

inline fun Context.componentName(className: String) =
    ComponentName(this, className)

inline fun ContextAware.componentName(className: String) =
    providedContext.componentName(className)

inline fun Fragment.componentName(className: String) =
    requireContext().componentName(className)

inline fun View.componentName(className: String) =
    context.componentName(className)

inline fun Context.startForegroundServiceCompat(intent: Intent) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startForegroundService(intent)
    } else {
        startService(intent)
    }
}

inline fun ContextAware.startForegroundServiceCompat(intent: Intent) {
    providedContext.startForegroundServiceCompat(intent)
}

inline fun Fragment.startForegroundServiceCompat(intent: Intent) {
    requireContext().startForegroundServiceCompat(intent)
}

inline fun View.startForegroundServiceCompat(intent: Intent) {
    context.startForegroundServiceCompat(intent)
}

fun Context.registerReceiver(
    intentFilter: IntentFilter,
    onReceive: (intent: Intent) -> Unit
): BroadcastReceiver {
    return object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            onReceive(intent)
        }
    }.also { registerReceiver(it, intentFilter) }
}

fun ContextAware.registerReceiver(
    intentFilter: IntentFilter,
    onReceive: (intent: Intent) -> Unit
) = providedContext.registerReceiver(intentFilter, onReceive)

fun Context.hasPermissions(vararg permissions: String): Boolean {
    return permissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }
}

fun ContextAware.hasPermissions(vararg permissions: String) =
    providedContext.hasPermissions(*permissions)

fun Fragment.hasPermissions(vararg permissions: String) =
    requireContext().hasPermissions(*permissions)

fun View.hasPermissions(vararg permissions: String) =
    context.hasPermissions(*permissions)

inline fun Context.isAppInstalled(packageName: String) =
    packageManager.isAppInstalled(packageName)

inline fun ContextAware.isAppInstalled(packageName: String) =
    providedContext.isAppInstalled(packageName)

inline fun Fragment.isAppInstalled(packageName: String) =
    requireContext().isAppInstalled(packageName)

inline fun View.isAppInstalled(packageName: String) =
    context.isAppInstalled(packageName)

fun Context.isAppLaunchable(packageName: String) =
    packageManager.isAppLaunchable(packageName)

fun ContextAware.isAppLaunchable(packageName: String) =
    providedContext.isAppLaunchable(packageName)

fun Fragment.isAppLaunchable(packageName: String) =
    requireContext().isAppLaunchable(packageName)

fun View.isAppLaunchable(packageName: String) =
    context.isAppLaunchable(packageName)

fun Context.isAppEnabled(packageName: String) =
    packageManager.isAppEnabled(packageName)

fun ContextAware.isAppEnabled(packageName: String) =
    providedContext.isAppEnabled(packageName)

fun Fragment.isAppEnabled(packageName: String) =
    requireContext().isAppEnabled(packageName)

fun View.isAppEnabled(packageName: String) =
    context.isAppEnabled(packageName)

inline fun <reified T : Application> Context.app() = applicationContext as T

inline fun <reified T : Application> ContextAware.app() = providedContext.app<T>()

inline fun <reified T : Application> Fragment.app() = requireContext().app<T>()

inline fun <reified T : Application> View.app() = context.app<T>()