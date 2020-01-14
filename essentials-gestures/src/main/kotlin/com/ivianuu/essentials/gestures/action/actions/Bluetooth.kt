package com.ivianuu.essentials.gestures.action.actions

import android.bluetooth.BluetoothAdapter
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

internal val EsBluetoothActionModule = Module {
    if (BluetoothAdapter.getDefaultAdapter() != null) {
        bindAction(
            key = "bluetooth",
            title = { getStringResource(R.string.es_action_bluetooth) },
            iconProvider = { get<BluetoothActionIconProvider>() },
            executor = { get<BluetoothActionExecutor>() }
        )
    }
}

@Factory
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

@Factory
internal class BluetoothActionIconProvider(
    private val broadcastFactory: BroadcastFactory,
    private val resourceProvider: ResourceProvider
) : ActionIconProvider {
    override val icon: Flow<ActionIcon>
        get() = broadcastFactory.create(BluetoothAdapter.ACTION_STATE_CHANGED)
            .map { it.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF) }
            .onStart { emit(BluetoothAdapter.getDefaultAdapter()?.state ?: BluetoothAdapter.STATE_OFF) }
            .map { it == BluetoothAdapter.STATE_ON || it == BluetoothAdapter.STATE_TURNING_ON }
            .map { if (it) R.drawable.es_ic_bluetooth else R.drawable.es_ic_bluetooth_disabled }
            .map { resourceProvider.getDrawable(it) }
            .map { ActionIcon(it) }
}
