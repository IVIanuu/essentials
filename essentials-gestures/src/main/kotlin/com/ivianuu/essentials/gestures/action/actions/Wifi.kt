package com.ivianuu.essentials.gestures.action.actions

import android.net.wifi.WifiManager
import com.ivianuu.essentials.broadcast.BroadcastFactory
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.ActionIconProvider
import com.ivianuu.essentials.gestures.action.bindAction
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

internal val EsWifiActionModule = Module {
    bindAction(
        key = "wifi",
        title = { getStringResource(R.string.es_action_wifi) },
        iconProvider = { get<WifiActionIconProvider>() },
        executor = { get<WifiActionExecutor>() }
    )
}

@Factory
internal class WifiActionExecutor(private val wifiManager: WifiManager) :
    ActionExecutor {
    override suspend fun invoke() {
        wifiManager.isWifiEnabled = !wifiManager.isWifiEnabled
    }
}

@Factory
internal class WifiActionIconProvider(
    broadcastFactory: BroadcastFactory,
    private val resourceProvider: ResourceProvider,
    private val wifiManager: WifiManager
) : ActionIconProvider {
    override val icon: Flow<ActionIcon> = broadcastFactory.create(WifiManager.WIFI_STATE_CHANGED_ACTION)
        .map {
            val state =
                it.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED)
            state == WifiManager.WIFI_STATE_ENABLED
        }
        .onStart { emit(wifiManager.isWifiEnabled) }
        .map { wifiEnabled ->
            ActionIcon(
                image = resourceProvider.getDrawable(
                    if (wifiEnabled) R.drawable.es_ic_network_wifi
                    else R.drawable.es_ic_signal_wifi_off
                )
            )
        }
}
