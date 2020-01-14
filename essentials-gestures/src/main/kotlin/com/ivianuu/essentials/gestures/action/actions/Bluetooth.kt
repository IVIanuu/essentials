package com.ivianuu.essentials.gestures.action.actions

/**
import android.bluetooth.BluetoothAdapter
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import kotlinx.coroutines.flow.Flow

private fun createBluetoothAction() = Action(
    key = KEY_BLUETOOTH,
    title = string(R.string.action_bluetooth),
    states = onOff(
        R.drawable.es_ic_bluetooth,
        R.drawable.es_ic_bluetooth_disabled
    )
)

private fun createBluetoothFlow(): Flow<String> {
    val adapter = BluetoothAdapter.getDefaultAdapter()
    val initialState = adapter?.state ?: BluetoothAdapter.STATE_OFF

    return broadcastFactory.create(BluetoothAdapter.ACTION_STATE_CHANGED)
        .map { it.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF) }
        .onStart { emit(initialState) }
        .map { it == BluetoothAdapter.STATE_ON || it == BluetoothAdapter.STATE_TURNING_ON }
        .onOffStateFromBoolean()
}

private fun toggleBluetooth() {
    BluetoothAdapter.getDefaultAdapter()?.let {
        if (it.isEnabled) {
            it.disable()
        } else {
            it.enable()
        }
    }
}*/
