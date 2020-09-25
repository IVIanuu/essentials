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

package com.ivianuu.essentials.twilight

import android.content.ComponentCallbacks2
import android.content.Intent
import android.content.res.Configuration
import android.os.PowerManager
import com.ivianuu.essentials.app.androidApplicationContext
import com.ivianuu.essentials.broadcast.BroadcastFactory
import com.ivianuu.essentials.coroutines.offerSafe
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.android.ApplicationResources
import com.ivianuu.injekt.given
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.util.Calendar

@Reader
val twilightState: Flow<TwilightState>
    get() = given<TwilightPrefs>().twilightMode.data
        .flatMapLatest { mode ->
            when (mode) {
                TwilightMode.System -> system()
                TwilightMode.Light -> flowOf(false)
                TwilightMode.Dark -> flowOf(true)
                TwilightMode.Battery -> battery()
                TwilightMode.Time -> time()
            }
        }
        .combine(given<TwilightPrefs>().useBlack.data) { isDark, useBlack ->
            TwilightState(isDark, useBlack)
        }
        .distinctUntilChanged()

@Reader
private fun battery() = BroadcastFactory.create(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
    .map { Unit }
    .onStart { emit(Unit) }
    .map { given<PowerManager>().isPowerSaveMode }

@Reader
private fun system() = configChanges()
    .onStart { emit(Unit) }
    .map {
        (given<ApplicationResources>().configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration
            .UI_MODE_NIGHT_YES
    }

@Reader
private fun time() = BroadcastFactory.create(Intent.ACTION_TIME_TICK)
    .map { Unit }
    .onStart { emit(Unit) }
    .map {
        val calendar = Calendar.getInstance()
        val hour = calendar[Calendar.HOUR_OF_DAY]
        hour < 6 || hour >= 22
    }

@Reader
private fun configChanges() = callbackFlow<Unit> {
    val callbacks = object : ComponentCallbacks2 {
        override fun onConfigurationChanged(newConfig: Configuration) {
            offerSafe(Unit)
        }

        override fun onLowMemory() {
        }

        override fun onTrimMemory(level: Int) {
        }
    }
    androidApplicationContext.registerComponentCallbacks(callbacks)
    awaitClose { androidApplicationContext.unregisterComponentCallbacks(callbacks) }
}

data class TwilightState(
    val isDark: Boolean,
    val useBlack: Boolean
)
