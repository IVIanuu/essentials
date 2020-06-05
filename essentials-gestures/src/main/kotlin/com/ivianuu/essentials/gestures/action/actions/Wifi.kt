package com.ivianuu.essentials.gestures.action.actions

import android.net.wifi.WifiManager
import androidx.compose.Composable
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Wifi
import androidx.ui.material.icons.filled.WifiOff
import com.ivianuu.essentials.broadcast.BroadcastFactory
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionIconProvider
import com.ivianuu.essentials.gestures.action.action
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.StringKey
import com.ivianuu.injekt.Transient
import com.ivianuu.injekt.composition.installIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

@Module
private fun wifiActionModule() {
    installIn<ApplicationComponent>()
    action { resourceProvider: ResourceProvider,
             iconProvider: WifiActionIconProvider,
             executor: WifiActionExecutor ->
        Action(
            key = "wifi",
            title = resourceProvider.getString(R.string.es_action_wifi),
            iconProvider = iconProvider,
            executor = executor
        ) as @StringKey("wifi") Action
    }
}

@Transient
internal class WifiActionExecutor(private val wifiManager: WifiManager) :
    ActionExecutor {
    override suspend fun invoke() {
        wifiManager.isWifiEnabled = !wifiManager.isWifiEnabled
    }
}

@Transient
internal class WifiActionIconProvider(
    broadcastFactory: BroadcastFactory,
    private val wifiManager: WifiManager
) : ActionIconProvider {
    override val icon: Flow<@Composable () -> Unit> =
        broadcastFactory.create(WifiManager.WIFI_STATE_CHANGED_ACTION)
            .map {
                val state =
                    it.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED)
                state == WifiManager.WIFI_STATE_ENABLED
            }
            .onStart { emit(wifiManager.isWifiEnabled) }
        .map { wifiEnabled ->
            if (wifiEnabled) Icons.Default.Wifi
            else Icons.Default.WifiOff
        }
        .map {
            {
                Icon(it)
            }
        }
}
