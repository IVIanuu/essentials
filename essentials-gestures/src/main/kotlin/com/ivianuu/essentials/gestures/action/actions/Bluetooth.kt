package com.ivianuu.essentials.gestures.action.actions

import android.bluetooth.BluetoothAdapter
import androidx.compose.foundation.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothDisabled
import com.ivianuu.essentials.broadcast.BroadcastFactory
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.GivenAction
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.Reader
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

@GivenAction
fun bluetoothAction() = Action(
    key = "bluetooth",
    title = Resources.getString(R.string.es_action_bluetooth),
    icon = bluetoothIcon(),
    enabled = BluetoothAdapter.getDefaultAdapter() != null,
    execute = { toggleBluetooth() }
)

@Reader
private fun toggleBluetooth() {
    BluetoothAdapter.getDefaultAdapter()?.let {
        if (it.isEnabled) {
            it.disable()
        } else {
            it.enable()
        }
    }
}

@Reader
private fun bluetoothIcon(): ActionIcon =
    BroadcastFactory.create(BluetoothAdapter.ACTION_STATE_CHANGED)
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
