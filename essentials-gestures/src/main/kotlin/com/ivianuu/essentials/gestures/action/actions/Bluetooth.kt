/*
 * Copyright 2020 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.bluetooth.*
import androidx.compose.material.*
import androidx.compose.ui.res.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.broadcast.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

@Given
object BluetoothActionId : ActionId("bluetooth")

@Given
fun bluetoothAction(
    @Given bluetoothIcon: Flow<BluetoothIcon>,
    @Given stringResource: StringResourceProvider,
) = Action<BluetoothActionId>(
    id = BluetoothActionId,
    title = stringResource(R.string.es_action_bluetooth, emptyList()),
    icon = bluetoothIcon,
    enabled = BluetoothAdapter.getDefaultAdapter() != null
)

@Given
fun bluetoothActionExecutor(): ActionExecutor<BluetoothActionId> = {
    BluetoothAdapter.getDefaultAdapter()?.let {
        if (it.isEnabled) {
            it.disable()
        } else {
            it.enable()
        }
    }
}

internal typealias BluetoothIcon = ActionIcon

@Given
fun bluetoothIcon(@Given broadcastsFactory: BroadcastsFactory): Flow<BluetoothIcon> {
    return broadcastsFactory(BluetoothAdapter.ACTION_STATE_CHANGED)
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
        .map { { Icon(painterResource(it), null) } }
}
