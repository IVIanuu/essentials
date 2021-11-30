/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.bluetooth.BluetoothAdapter
import androidx.compose.material.Icon
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.loadResource
import com.ivianuu.essentials.state.valueFromFlow
import com.ivianuu.essentials.util.BroadcastsFactory
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

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
  val bluetoothEnabled = valueFromFlow(true) {
    broadcastsFactory(BluetoothAdapter.ACTION_STATE_CHANGED)
      .map { it.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF) }
      .onStart {
        emit(
          BluetoothAdapter.getDefaultAdapter()?.state ?: BluetoothAdapter.STATE_OFF
        )
      }
      .map { it == BluetoothAdapter.STATE_ON || it == BluetoothAdapter.STATE_TURNING_ON }
  }

  Icon(
    if (bluetoothEnabled) R.drawable.es_ic_bluetooth
    else R.drawable.es_ic_bluetooth_disabled
  )
}
