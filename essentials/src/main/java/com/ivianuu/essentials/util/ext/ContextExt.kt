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

import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.BatteryManager
import android.os.Build
import android.os.PowerManager
import android.support.v4.content.ContextCompat
import com.ivianuu.essentials.util.ContextAware

inline val Context.isTablet: Boolean
    get() = resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE

inline val ContextAware.isTablet get() = providedContext.isTablet

inline val Context.hasNavigationBar: Boolean
    get() {
        val id = resources.getIdentifier("config_showNavigationBar", "bool", "android")
        return id > 0 && resources.getBoolean(id)
    }

inline val ContextAware.hasNavigationBar get() = providedContext.hasNavigationBar

inline val Context.isScreenOn: Boolean
    get() = systemService<PowerManager>().isInteractive

inline val ContextAware.isScreenOn get() = providedContext.isScreenOn

inline val Context.isScreenOff get() = !isScreenOn

inline val ContextAware.isScreenOff get() = providedContext.isScreenOff

inline val Context.isCharging: Boolean
    get() {
        val intentFilter = intentFilterOf(Intent.ACTION_BATTERY_CHANGED)
        val intent = registerReceiver(null, intentFilter) ?: return false
        val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        return plugged == BatteryManager.BATTERY_PLUGGED_AC
                || plugged == BatteryManager.BATTERY_PLUGGED_USB
                || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS
    }

inline val ContextAware.isCharging get() = providedContext.isCharging

inline val Context.batteryLevel: Int
    get() {
        val batteryIntent = registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        ) ?: return -1
        val level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

        return if (level == -1 || scale == -1) {
            -1
        } else
            (level.toFloat() / scale.toFloat() * 100.0f).toInt()
    }

inline val ContextAware.batteryLevel get() = providedContext.batteryLevel

inline fun <reified T> Context.componentFor() = ComponentName(this, T::class.java)

inline fun <reified T> ContextAware.componentFor() =
    providedContext.componentFor<T>()

inline fun Context.isAppInstalled(packageName: String): Boolean {
    return try {
        packageManager.getApplicationInfo(packageName, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
}

inline fun ContextAware.isAppInstalled(packageName: String) =
    providedContext.isAppInstalled(packageName)

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

inline fun Context.registerReceiver(
    intentFilter: IntentFilter,
    crossinline onReceive: (intent: Intent) -> Unit
): BroadcastReceiver {
    return object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            onReceive(intent)
        }
    }.also { registerReceiver(it, intentFilter) }
}

inline fun ContextAware.registerReceiver(
    intentFilter: IntentFilter,
    crossinline onReceive: (intent: Intent) -> Unit
) = providedContext.registerReceiver(intentFilter, onReceive)

inline fun Context.unregisterReceiverSafe(receiver: BroadcastReceiver) {
    try {
        unregisterReceiver(receiver)
    } catch (e: IllegalArgumentException) {
        // ignore
    }
}

inline fun ContextAware.unregisterReceiverSafe(receiver: BroadcastReceiver) {
    providedContext.unregisterReceiverSafe(receiver)
}

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }

    return context as? Activity
}

inline fun ContextAware.findActivity() = providedContext.findActivity()

inline fun Context.findActivityOrThrow() =
    findActivity() ?: throw IllegalStateException("base context is no activity")

inline fun ContextAware.findActivityOrThrow() =
    providedContext.findActivityOrThrow()

fun Context.hasPermissions(vararg permissions: String): Boolean {
    return permissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }
}

fun ContextAware.hasPermissions(vararg permissions: String) =
    providedContext.hasPermissions(*permissions)