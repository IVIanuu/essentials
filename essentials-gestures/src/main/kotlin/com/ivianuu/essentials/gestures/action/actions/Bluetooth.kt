package com.ivianuu.essentials.gestures.action.actions

/**
@Module
fun BluetoothModule() {
installIn<ApplicationComponent>()
    action {
            resourceProvider: ResourceProvider,
            iconProvider: BluetoothActionIconProvider,
            executor: BluetoothActionExecutor ->
        Action(
key = "bluetooth",
title = getString(R.string.es_action_bluetooth),
iconProvider = iconProvider,
            executor = executor,
            enabled = BluetoothAdapter.getDefaultAdapter() != null
        ) as @StringKey("bluetooth") Action
    }
}

@Given
internal class BluetoothActionExecutor : ActionExecutor {
    override suspend fun invoke() {
        BluetoothAdapter.getDefaultAdapter()?.let {
            if (it.isEnabled) {
                it.disable()
            } else {
                it.enable()
            }
        }
    }
}

@Given
internal class BluetoothActionIconProvider(
    private val broadcastFactory: BroadcastFactory
) : ActionIconProvider {
    override val icon: Flow<@Composable () -> Unit>
        get() = broadcastFactory.create(BluetoothAdapter.ACTION_STATE_CHANGED)
            .map { it.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF) }
            .onStart {
                emit(
                    BluetoothAdapter.getDefaultAdapter()?.state ?: BluetoothAdapter.STATE_OFF
                )
            }
            .map { it == BluetoothAdapter.STATE_ON || it == BluetoothAdapter.STATE_TURNING_ON }
            .map {
                if (it) Icons.Default.Bluetooth
                else Icons.Default.BluetoothDisabled
            }
            .map { { Icon(it) } }
}
 */