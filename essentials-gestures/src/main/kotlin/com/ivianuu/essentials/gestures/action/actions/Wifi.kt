/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */
package com.ivianuu.essentials.gestures.action.actions

import android.net.wifi.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.gestures.*
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.android.*
import kotlinx.coroutines.flow.*

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
