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
import android.os.Build
import androidx.core.content.ContextCompat
import com.ivianuu.essentials.util.ContextAware

inline fun <reified T> Context.componentName() = ComponentName(this, T::class.java)

inline fun <reified T> ContextAware.componentName() =
    providedContext.componentName<T>()

inline fun Context.componentName(className: String) =
    ComponentName(this, className)

inline fun ContextAware.componentName(className: String) =
    providedContext.componentName(className)

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

inline fun <reified T : Application> Context.app() = applicationContext as T

inline fun <reified T : Application> ContextAware.app() = providedContext.app<T>()