package com.ivianuu.essentials.util

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.time.Duration

interface SensorFactory {
  fun sensor(type: Int): Sensor?

  fun sensorEvents(sensor: Sensor, samplingRate: Duration): Flow<SensorEvent>
}

fun SensorFactory.sensorEvents(sensorType: Int, samplingRate: Duration): Flow<SensorEvent> =
  sensorEvents(sensor(sensorType)!!, samplingRate)

@Provide class SensorFactoryImpl(
  private val sensorManager: @SystemService SensorManager,
) : SensorFactory {
  override fun sensor(type: Int): Sensor? = sensorManager.getDefaultSensor(type)

  override fun sensorEvents(sensor: Sensor, samplingRate: Duration): Flow<SensorEvent> =
    callbackFlow {
      val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
          trySend(event)
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        }
      }

      sensorManager.registerListener(listener, sensor, samplingRate.inWholeNanoseconds.toInt())
      awaitClose { sensorManager.unregisterListener(listener) }
    }
}
