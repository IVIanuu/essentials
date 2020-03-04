package com.ivianuu.essentials.gestures.action.actions

import android.bluetooth.BluetoothAdapter
import androidx.compose.Composable
import androidx.ui.foundation.Icon
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Bluetooth
import androidx.ui.material.icons.filled.BluetoothDisabled
import com.ivianuu.essentials.broadcast.BroadcastFactory
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionIconProvider
import com.ivianuu.essentials.gestures.action.bindAction
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Module
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
    private val broadcastFactory: BroadcastFactory
) : ActionIconProvider {
    override val icon: Flow<@Composable () -> Unit>
        get() = broadcastFactory.create(BluetoothAdapter.ACTION_STATE_CHANGED)
            .map { it.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF) }
            .onStart { emit(BluetoothAdapter.getDefaultAdapter()?.state ?: BluetoothAdapter.STATE_OFF) }
            .map { it == BluetoothAdapter.STATE_ON || it == BluetoothAdapter.STATE_TURNING_ON }
            .map {
                if (it) Icons.Default.Bluetooth
                else Icons.Default.BluetoothDisabled
            }
            .map {
                {
                    Icon(it)
                }
            }
}
