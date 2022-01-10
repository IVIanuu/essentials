/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.bluetooth.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

@Provide object BluetoothActionId : ActionId("bluetooth")

@Provide fun bluetoothAction(
  B: BroadcastsFactory,
  RP: ResourceProvider
) = Action(
  id = BluetoothActionId,
  title = loadResource(R.string.es_action_bluetooth),
  icon = bluetoothIcon(),
  enabled = BluetoothAdapter.getDefaultAdapter() != null
)

@Provide val bluetoothActionExecutor = ActionExecutor<BluetoothActionId> {
  BluetoothAdapter.getDefaultAdapter()?.let {
    if (it.isEnabled) {
      it.disable()
    } else {
      it.enable()
    }
  }
}

private fun bluetoothIcon(@Inject broadcastsFactory: BroadcastsFactory) = ActionIcon {
  val bluetoothEnabled by remember {
    broadcastsFactory(BluetoothAdapter.ACTION_STATE_CHANGED)
      .map { it.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF) }
      .onStart {
        emit(
          BluetoothAdapter.getDefaultAdapter()?.state ?: BluetoothAdapter.STATE_OFF
        )
      }
      .map { it == BluetoothAdapter.STATE_ON || it == BluetoothAdapter.STATE_TURNING_ON }
  }
    .collectAsState(false)

  Icon(
    if (bluetoothEnabled) R.drawable.es_ic_bluetooth
    else R.drawable.es_ic_bluetooth_disabled
  )
}
