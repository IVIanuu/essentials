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
import com.ivianuu.essentials.broadcast.broadcasts
import com.ivianuu.essentials.coroutines.GlobalScope
import com.ivianuu.essentials.screenstate.ConfigChanges
import com.ivianuu.essentials.twilight.data.TwilightMode
import com.ivianuu.essentials.twilight.data.TwilightPrefsState
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.android.ApplicationResources
import com.ivianuu.injekt.merge.ApplicationComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

data class TwilightState(
    val isDark: Boolean,
    val useBlack: Boolean
)

typealias TwilightStateFlow = StateFlow<TwilightState>

@Binding(ApplicationComponent::class)
fun twilightState(
    globalScope: GlobalScope,
    batteryTwilightState: BatteryTwilightState,
    systemTwilightState: SystemTwilightState,
    timeTwilightState: TimeTwilightState,
    twilightPrefsState: Flow<TwilightPrefsState>
): TwilightStateFlow {
    return twilightPrefsState.flatMapLatest { (mode, useBlack) ->
        (when (mode) {
            TwilightMode.System -> systemTwilightState
            TwilightMode.Light -> flowOf(false)
            TwilightMode.Dark -> flowOf(true)
            TwilightMode.Battery -> batteryTwilightState
            TwilightMode.Time -> timeTwilightState
        }).map { TwilightState(it, useBlack) }
    }
        .distinctUntilChanged()
        .stateIn(globalScope, SharingStarted.Eagerly, TwilightState(false, false))
}

internal typealias BatteryTwilightState = Flow<Boolean>

@Binding
fun batteryTwilightState(
    broadcasts: broadcasts,
    powerManager: PowerManager,
): BatteryTwilightState {
    return broadcasts(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
        .map { Unit }
        .onStart { emit(Unit) }
        .map { powerManager.isPowerSaveMode }
}

internal typealias SystemTwilightState = Flow<Boolean>

@Binding
fun systemTwilightState(
    configChanges: ConfigChanges,
    applicationResources: ApplicationResources,
): SystemTwilightState = configChanges
    .onStart { emit(Unit) }
    .map {
        (applicationResources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration
            .UI_MODE_NIGHT_YES
    }

internal typealias TimeTwilightState = Flow<Boolean>

@Binding
fun timeTwilightState(
    broadcasts: broadcasts,
): TimeTwilightState = broadcasts(Intent.ACTION_TIME_TICK)
    .map { Unit }
    .onStart { emit(Unit) }
    .map {
        val calendar = Calendar.getInstance()
        val hour = calendar[Calendar.HOUR_OF_DAY]
        hour < 6 || hour >= 22
    }
