/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */
package com.ivianuu.essentials.gestures.action.actions

import android.net.wifi.WifiManager
import androidx.compose.material.Icon
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.loadResource
import com.ivianuu.essentials.util.BroadcastsFactory
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

@Provide object WifiActionId : ActionId("wifi")

@Provide fun wifiAction(icon: WifiIcon, RP: ResourceProvider) = Action(
  id = WifiActionId,
  title = loadResource(R.string.es_action_wifi),
  icon = icon
)

@Provide fun wifiActionExecutor(
  wifiManager: @SystemService WifiManager
) = ActionExecutor<WifiActionId> {
  @Suppress("DEPRECATION")
  wifiManager.isWifiEnabled = !wifiManager.isWifiEnabled
}

fun interface WifiIcon : ActionIcon

@Provide fun wifiIcon(
  broadcastsFactory: BroadcastsFactory,
  wifiManager: @SystemService WifiManager,
) = WifiIcon {
  val wifiEnabled by remember {
    broadcastsFactory(WifiManager.WIFI_STATE_CHANGED_ACTION)
      .map {
        val state =
          it.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED)
        state == WifiManager.WIFI_STATE_ENABLED
      }
      .onStart { emit(wifiManager.isWifiEnabled) }
  }.collectAsState(false)

  Icon(
    if (wifiEnabled) R.drawable.es_ic_network_wifi
    else R.drawable.es_ic_signal_wifi_off
  )
}
