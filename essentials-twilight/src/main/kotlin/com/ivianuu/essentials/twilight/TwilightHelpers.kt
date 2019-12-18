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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.PowerManager
import androidx.compose.Composable
import androidx.compose.ambient
import androidx.compose.onActive
import androidx.compose.remember
import androidx.compose.state
import androidx.ui.core.ConfigurationAmbient
import androidx.ui.core.ContextAmbient
import com.ivianuu.essentials.ui.box.unfoldBox
import com.ivianuu.essentials.ui.injekt.inject
import java.util.Calendar

@Composable
internal fun isDark(): Boolean {
    val prefs = inject<TwilightPrefs>()
    val twilightMode = unfoldBox(prefs.twilightMode)
    return when (twilightMode.value) {
        TwilightMode.System -> isSystemSetToDarkTheme()
        TwilightMode.Light -> false
        TwilightMode.Dark -> true
        TwilightMode.Battery -> isInPowerSaveMode()
        TwilightMode.Time -> isNight()
    }
}

@Composable
private fun isInPowerSaveMode(): Boolean {
    val context = ambient(ContextAmbient)
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    val isPowerSaveMode = state { powerManager.isPowerSaveMode }

    val broadcastReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                isPowerSaveMode.value = powerManager.isPowerSaveMode
            }
        } as BroadcastReceiver // todo remove once fixed
    }

    val intentFilter = remember {
        IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)
    }

    onActive {
        try {
            context.registerReceiver(broadcastReceiver, intentFilter)
        } catch (e: Exception) {
            // already registered
        }
        onDispose {
            try {
                context.unregisterReceiver(broadcastReceiver)
            } catch (e: Exception) {
                // already unregistered
            }
        }
    }

    return isPowerSaveMode.value
}

@Composable
private fun isSystemSetToDarkTheme(): Boolean {
    val configuration = ambient(ConfigurationAmbient)
    return (configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration
        .UI_MODE_NIGHT_YES
}

@Composable
private fun isNight(): Boolean {
    val calendar = Calendar.getInstance()
    val hour = calendar[Calendar.HOUR_OF_DAY]
    return hour < 6 || hour >= 22

}