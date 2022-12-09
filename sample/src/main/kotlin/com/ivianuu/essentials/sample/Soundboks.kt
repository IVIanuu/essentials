/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService

@Provide fun lol(
  context: AppContext,
  bluetoothManager: @SystemService BluetoothManager,
  logger: Logger
) = ScopeWorker<AppScope> {
  val adapter = BluetoothAdapter.getDefaultAdapter()

  adapter.bondedDevices
    .filter { it.name.contains("#") }
    .forEach { device ->
      device.connectGatt(
        context,
        false,
        object : BluetoothGattCallback() {
          override fun onServiceChanged(gatt: BluetoothGatt) {
            super.onServiceChanged(gatt)
            log { "on service changed" }
          }

          override fun onConnectionStateChange(
            gatt: BluetoothGatt,
            status: Int,
            newState: Int
          ) {
            super.onConnectionStateChange(gatt, status, newState)
            if (newState == BluetoothProfile.STATE_CONNECTED) {
              gatt.discoverServices()
              log { "${gatt.device.name} ever?" }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
              log { "not connected" }
            }

            log { "hello ${gatt.device.name} $status $newState" }
          }

          override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            log { "${gatt.device.name} got services" }
          }

          override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
          ) {
            super.onCharacteristicChanged(gatt, characteristic)
            log { "${gatt.device.name} on char changed $gatt ${characteristic.value}" }
          }
        }
      ).connect().also { require(it) }
    }
}