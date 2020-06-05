package com.ivianuu.essentials.gestures.action.actions

import android.bluetooth.BluetoothAdapter
import androidx.compose.Composable
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Bluetooth
import androidx.ui.material.icons.filled.BluetoothDisabled
import com.ivianuu.essentials.broadcast.BroadcastFactory
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionIconProvider
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Transient
import com.ivianuu.injekt.composition.installIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

@Module
private fun BluetoothModule() {
    installIn<ApplicationComponent>()
    /*bindAction<@ActionQualifier("bluetooth") Action>(
        key = "bluetooth",
        title = { getStringResource(R.string.es_action_bluetooth) },
        iconProvider = { get<BluetoothActionIconProvider>() },
        executor = { get<BluetoothActionExecutor>() },
        enabled = { BluetoothAdapter.getDefaultAdapter() != null }
    )*/
}

@Transient
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

@Transient
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
