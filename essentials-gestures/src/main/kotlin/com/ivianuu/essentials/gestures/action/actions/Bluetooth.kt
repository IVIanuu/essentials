package com.ivianuu.essentials.gestures.action.actions

import android.bluetooth.BluetoothAdapter
import androidx.compose.foundation.Icon
import androidx.compose.ui.res.vectorResource
import com.ivianuu.essentials.broadcast.BroadcastFactory
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

@ActionBinding
fun bluetoothAction(
    bluetoothIcon: bluetoothIcon,
    resources: Resources,
) = Action(
    key = "bluetooth",
    title = resources.getString(R.string.es_action_bluetooth),
    icon = bluetoothIcon(),
    enabled = BluetoothAdapter.getDefaultAdapter() != null,
    execute = { toggleBluetooth() }
)

@FunBinding
internal fun toggleBluetooth() {
    BluetoothAdapter.getDefaultAdapter()?.let {
        if (it.isEnabled) {
            it.disable()
        } else {
            it.enable()
        }
    }
}

@FunBinding
fun bluetoothIcon(
    broadcastFactory: BroadcastFactory,
): ActionIcon {
    return broadcastFactory.create(BluetoothAdapter.ACTION_STATE_CHANGED)
        .map { it.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF) }
        .onStart {
            emit(
                BluetoothAdapter.getDefaultAdapter()?.state ?: BluetoothAdapter.STATE_OFF
            )
        }
        .map { it == BluetoothAdapter.STATE_ON || it == BluetoothAdapter.STATE_TURNING_ON }
        .map {
            if (it) R.drawable.es_ic_bluetooth
            else R.drawable.es_ic_bluetooth_disabled
        }
        .map { { Icon(vectorResource(it)) } }
}
