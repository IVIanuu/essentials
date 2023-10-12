package com.ivianuu.essentials.util

import android.hardware.Sensor
import android.hardware.SensorEvent as AndroidSensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.TriggerEvent
import android.hardware.TriggerEventListener
import com.ivianuu.essentials.SystemService
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.nanoseconds

interface SensorFactory {
  fun sensor(type: Int): Sensor?

  fun sensorEvents(sensor: Sensor, samplingRate: Duration): Flow<SensorEvent>
}

data class SensorEvent(
  var sensor: Sensor,
  var timestamp: Duration = Duration.ZERO,
  val values: FloatArray = floatArrayOf(),
  var accuracy: Int = 0
)

fun SensorFactory.sensorEvents(sensorType: Int, samplingRate: Duration): Flow<SensorEvent> =
  sensorEvents(sensor(sensorType)!!, samplingRate)

@Provide class SensorFactoryImpl(
  private val sensorManager: @SystemService SensorManager,
  private val wakeLockManager: WakeLockManager,
) : SensorFactory {
  override fun sensor(type: Int): Sensor? = sensorManager.getDefaultSensor(type)

  override fun sensorEvents(sensor: Sensor, samplingRate: Duration): Flow<SensorEvent> =
    callbackFlow {
      if (!sensor.isWakeUpSensor)
        launch { wakeLockManager.acquire() }

      if (sensor.reportingMode == Sensor.REPORTING_MODE_ONE_SHOT) {
        val listener = object : TriggerEventListener() {
          override fun onTrigger(event: TriggerEvent) {
            trySend(
              SensorEvent(
                sensor = event.sensor,
                timestamp = event.timestamp.nanoseconds,
                values = event.values
              )
            )

            sensorManager.requestTriggerSensor(this, sensor)
          }
        }

        sensorManager.requestTriggerSensor(listener, sensor)
        awaitClose { sensorManager.cancelTriggerSensor(listener, sensor) }
      } else {
        val listener = object : SensorEventListener {
          override fun onSensorChanged(event: AndroidSensorEvent) {
            trySend(
              SensorEvent(
                sensor = event.sensor,
                timestamp = event.timestamp.nanoseconds,
                values = event.values,
                accuracy = event.accuracy
              )
            )
          }

          override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
          }
        }

        sensorManager.registerListener(listener, sensor, samplingRate.inWholeNanoseconds.toInt())
        awaitClose { sensorManager.unregisterListener(listener) }
      }
    }
}
