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
import android.os.Build
import android.support.v4.content.ContextCompat
import com.ivianuu.essentials.util.ContextAware

inline fun Context.startForegroundServiceCompat(intent: Intent) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startForegroundService(intent)
    } else {
        startService(intent)
    }
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

inline fun Context.unregisterReceiverSafe(receiver: BroadcastReceiver) {
    try {
        unregisterReceiver(receiver)
    } catch (e: IllegalArgumentException) {
        // ignore
    }
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

inline fun Context.findActivityOrThrow() =
    findActivity() ?: throw IllegalStateException("base context is no activity")

fun Context.hasPermissions(vararg permissions: String): Boolean {
    return permissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }
}

fun ContextAware.hasPermissions(vararg permissions: String) =
    providedContext.hasPermissions(*permissions)