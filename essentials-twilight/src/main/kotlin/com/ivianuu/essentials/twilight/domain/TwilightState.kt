/*
 * Copyright 2020 Manuel Wrage
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

package com.ivianuu.essentials.twilight.domain

import android.content.*
import android.content.res.*
import android.os.*
import com.ivianuu.essentials.broadcast.*
import com.ivianuu.essentials.screenstate.*
import com.ivianuu.essentials.twilight.data.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.coroutines.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.flow.*
import java.util.*

data class TwilightState(
    val isDark: Boolean = false,
    val useBlack: Boolean = false,
)

@Given
fun twilightState(
    @Given scope: GivenCoroutineScope<AppGivenScope>,
    @Given batteryTwilightState: () -> Flow<BatteryTwilightState>,
    @Given systemTwilightState: () -> Flow<SystemTwilightState>,
    @Given timeTwilightState: () -> Flow<TimeTwilightState>,
    @Given twilightPrefs: Flow<TwilightPrefs>,
): @Eager<AppGivenScope> StateFlow<TwilightState> = twilightPrefs
    .flatMapLatest { (mode, useBlack) ->
        (when (mode) {
            TwilightMode.SYSTEM -> systemTwilightState()
            TwilightMode.LIGHT -> flowOf(false)
            TwilightMode.DARK -> flowOf(true)
            TwilightMode.BATTERY -> batteryTwilightState()
            TwilightMode.TIME -> timeTwilightState()
        }).map { TwilightState(it, useBlack) }
    }
    .distinctUntilChanged()
    .stateIn(scope, SharingStarted.Eagerly, TwilightState(false, false))

typealias BatteryTwilightState = Boolean

@Given
fun batteryTwilightState(
    @Given broadcastsFactory: BroadcastsFactory,
    @Given powerManager: @SystemService PowerManager,
): Flow<BatteryTwilightState> = broadcastsFactory(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
    .map { Unit }
    .onStart { emit(Unit) }
    .map { powerManager.isPowerSaveMode }

typealias SystemTwilightState = Boolean

@Given
fun systemTwilightState(
    @Given configChanges: Flow<ConfigChange>,
    @Given resources: AppResources,
): Flow<SystemTwilightState> = configChanges
    .onStart { emit(ConfigChange) }
    .map {
        (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration
            .UI_MODE_NIGHT_YES
    }

typealias TimeTwilightState = Boolean

@Given
fun timeTwilightState(
    @Given broadcastsFactory: BroadcastsFactory,
): Flow<TimeTwilightState> = broadcastsFactory(Intent.ACTION_TIME_TICK)
    .map { Unit }
    .onStart { emit(Unit) }
    .map {
        val calendar = Calendar.getInstance()
        val hour = calendar[Calendar.HOUR_OF_DAY]
        hour < 6 || hour >= 22
    }
