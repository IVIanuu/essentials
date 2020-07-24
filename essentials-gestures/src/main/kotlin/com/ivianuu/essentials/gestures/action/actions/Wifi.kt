package com.ivianuu.essentials.gestures.action.actions

import android.net.wifi.WifiManager
import androidx.ui.foundation.Icon
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Wifi
import androidx.ui.material.icons.filled.WifiOff
import com.ivianuu.essentials.broadcast.BroadcastFactory
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.BindAction
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

@BindAction
@Reader
fun wifiAction() = Action(
    key = "wifi",
    title = Resources.getString(R.string.es_action_wifi),
    icon = wifiIcon(),
    execute = { toggleWifi() }
)

@Reader
private fun toggleWifi() {
    val wifiManager = given<WifiManager>()
    wifiManager.isWifiEnabled = !wifiManager.isWifiEnabled
}

@Reader
private fun wifiIcon(): ActionIcon = BroadcastFactory.create(WifiManager.WIFI_STATE_CHANGED_ACTION)
    .map {
        val state =
            it.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED)
        state == WifiManager.WIFI_STATE_ENABLED
    }
    .onStart { emit(given<WifiManager>().isWifiEnabled) }
    .map { wifiEnabled ->
        if (wifiEnabled) Icons.Default.Wifi
        else Icons.Default.WifiOff
    }
    .map {
        {
            Icon(it)
        }
    }
