/*
 * Copyright 2021 Manuel Wrage
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
import com.ivianuu.essentials.Res
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.loadResource
import com.ivianuu.essentials.util.BroadcastsFactory
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.android.SystemService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

@Provide object WifiActionId : ActionId("wifi")

@Provide @Res fun wifiAction(icon: Flow<WifiIcon>) = Action(
  id = WifiActionId,
  title = loadResource(R.string.es_action_wifi),
  icon = icon
)

@Provide fun wifiActionExecutor(
  wifiManager: @SystemService WifiManager
): ActionExecutor<WifiActionId> = {
  @Suppress("DEPRECATION")
  wifiManager.isWifiEnabled = !wifiManager.isWifiEnabled
}

@Tag annotation class WifiIconTag
typealias WifiIcon = @WifiIconTag ActionIcon

@Provide fun wifiIcon(
  broadcastsFactory: BroadcastsFactory,
  wifiManager: @SystemService WifiManager,
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
      Icon(it)
    }
  }
