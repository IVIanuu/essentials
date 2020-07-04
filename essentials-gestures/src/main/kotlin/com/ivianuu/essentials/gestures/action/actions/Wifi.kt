package com.ivianuu.essentials.gestures.action.actions

/**
@Module
fun wifiActionModule() {
installIn<ApplicationComponent>()
    action { resourceProvider: ResourceProvider,
             iconProvider: WifiActionIconProvider,
             executor: WifiActionExecutor ->
        Action(
key = "wifi",
title = getString(R.string.es_action_wifi),
iconProvider = iconProvider,
            executor = executor
        ) as @StringKey("wifi") Action
    }
}

@Unscoped
internal class WifiActionExecutor(private val wifiManager: WifiManager) :
    ActionExecutor {
    override suspend fun invoke() {
        wifiManager.isWifiEnabled = !wifiManager.isWifiEnabled
    }
}

@Unscoped
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
 */