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

import android.content.Intent
import android.content.res.Configuration
import android.os.PowerManager
import com.ivianuu.essentials.broadcast.BroadcastsFactory
import com.ivianuu.essentials.coroutines.ScopeCoroutineScope
import com.ivianuu.essentials.screenstate.ConfigChange
import com.ivianuu.essentials.twilight.data.TwilightMode
import com.ivianuu.essentials.twilight.data.TwilightPrefs
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.AppResources
import com.ivianuu.injekt.scope.AppGivenScope
import com.ivianuu.injekt.scope.Eager
import java.util.Calendar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

data class TwilightState(
    val isDark: Boolean = false,
    val useBlack: Boolean = false,
)

@Given
fun twilightState(
    @Given scope: ScopeCoroutineScope<AppGivenScope>,
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
    @Given powerManager: PowerManager,
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
