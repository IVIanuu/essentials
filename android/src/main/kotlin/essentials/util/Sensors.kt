package essentials.util

import android.hardware.*
import androidx.compose.runtime.*
import essentials.*
import injekt.*
import kotlin.time.*

@Provide class Sensors(
  private val sensorManager: @SystemService SensorManager,
  private val wakeLocks: WakeLocks
) {
  fun sensorOf(type: Int): Sensor? = sensorManager.getDefaultSensor(type)

  @Composable fun TriggerEventHandler(sensor: Sensor, onEvent: (TriggerEvent) -> Unit) {
    if (!sensor.isWakeUpSensor)
      wakeLocks.WakeLock("sensors")

    DisposableEffect(sensor) {
      val listener = object : TriggerEventListener() {
        override fun onTrigger(event: TriggerEvent) {
          onEvent(event)
          sensorManager.requestTriggerSensor(this, sensor)
        }
      }

      sensorManager.requestTriggerSensor(listener, sensor)
      onDispose { sensorManager.cancelTriggerSensor(listener, sensor) }
    }
  }

  @Composable fun SensorEventHandler(
    sensor: Sensor,
    samplingRate: Duration,
    onEvent: (SensorEvent) -> Unit
  ) {
    if (!sensor.isWakeUpSensor)
      wakeLocks.WakeLock("sensors")

    DisposableEffect(sensor) {
      val listener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
          onEvent(event)
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        }
      }

      sensorManager.registerListener(listener, sensor, samplingRate.inWholeNanoseconds.toInt())
      onDispose { sensorManager.unregisterListener(listener) }
    }
  }
}
