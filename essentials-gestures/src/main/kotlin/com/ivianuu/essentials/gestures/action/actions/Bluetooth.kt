package com.ivianuu.essentials.gestures.action.actions

import android.bluetooth.BluetoothAdapter
import androidx.compose.Composable
import androidx.ui.foundation.Icon
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Bluetooth
import androidx.ui.material.icons.filled.BluetoothDisabled
import com.ivianuu.essentials.broadcast.BroadcastFactory
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionIconProvider
import com.ivianuu.essentials.gestures.action.BindAction
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

@BindAction
@Reader
fun bluetoothAction() = Action(
    key = "bluetooth",
    title = Resources.getString(R.string.es_action_bluetooth),
    iconProvider = given<BluetoothActionIconProvider>(),
    executor = given<BluetoothActionExecutor>(),
    enabled = BluetoothAdapter.getDefaultAdapter() != null
)

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
internal class BluetoothActionIconProvider : ActionIconProvider {
    override val icon: Flow<@Composable () -> Unit>
        get() = BroadcastFactory.create(BluetoothAdapter.ACTION_STATE_CHANGED)
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
