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
package com.ivianuu.essentials.gestures.action.actions

import android.net.wifi.WifiManager
import androidx.compose.material.Icon
import androidx.compose.ui.res.painterResource
import com.ivianuu.essentials.broadcast.BroadcastsFactory
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionExecutorBinding
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.Given
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

@Given
object WifiActionId : ActionId("wifi")

@ActionBinding<WifiActionId>
@Given
fun wifiAction(
    @Given resourceProvider: ResourceProvider,
    @Given wifiIcon: Flow<WifiIcon>,
) = Action(
    id = WifiActionId,
    title = resourceProvider.string(R.string.es_action_wifi),
    icon = wifiIcon
)

@ActionExecutorBinding<WifiActionId>
@Given
fun wifiActionExecutor(@Given wifiManager: WifiManager): ActionExecutor = {
    @Suppress("DEPRECATION")
    wifiManager.isWifiEnabled = !wifiManager.isWifiEnabled
}

typealias WifiIcon = ActionIcon

@Given
fun wifiIcon(
    @Given broadcastsFactory: BroadcastsFactory,
    @Given wifiManager: WifiManager,
): Flow<WifiIcon> = broadcastsFactory(WifiManager.WIFI_STATE_CHANGED_ACTION)
    .map {
        val state =
            it.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED)
        state == WifiManager.WIFI_STATE_ENABLED
    }
    .onStart { emit(wifiManager.isWifiEnabled) }
    .map { wifiEnabled ->
        if (wifiEnabled) R.drawable.es_ic_network_wifi
        else R.drawable.es_ic_signal_wifi_off
    }
    .map {
        {
            Icon(painterResource(it), null)
        }
    }
