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

import android.bluetooth.BluetoothAdapter
import com.ivianuu.essentials.broadcast.broadcasts
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.ui.core.Icon
import com.ivianuu.essentials.util.stringResource
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

object BluetoothActionId : ActionId("bluetooth")

@ActionBinding<BluetoothActionId>
@Given
fun bluetoothAction(
    @Given bluetoothIcon: Flow<BluetoothIcon>,
    @Given stringResource: stringResource,
): Action = Action(
    id = BluetoothActionId,
    title = stringResource(R.string.es_action_bluetooth),
    icon = bluetoothIcon,
    enabled = BluetoothAdapter.getDefaultAdapter() != null
)

@ActionExecutorBinding<BluetoothActionId>
@GivenFun
suspend fun toggleBluetooth() {
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
fun bluetoothIcon(@Given broadcasts: broadcasts): Flow<BluetoothIcon> {
    return broadcasts(BluetoothAdapter.ACTION_STATE_CHANGED)
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
        .map { { Icon(it, null) } }
}
