package essentials.util

import android.hardware.*
import androidx.compose.runtime.*
import essentials.*
import injekt.*
import kotlin.time.*

fun sensor(type: Int, scope: Scope<*> = inject): Sensor? =
  systemService<SensorManager>().getDefaultSensor(type)

@Composable fun TriggerEventHandler(
  sensor: Sensor,
  scope: Scope<*> = inject,
  onEvent: (TriggerEvent) -> Unit
) {
  if (!sensor.isWakeUpSensor)
    WakeLock("sensors")

  DisposableEffect(sensor) {
    val sensorManager = systemService<SensorManager>()
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
  scope: Scope<*> = inject,
  onEvent: (SensorEvent) -> Unit
) {
  if (!sensor.isWakeUpSensor)
    WakeLock("sensors")

  DisposableEffect(sensor) {
    val listener = object : SensorEventListener {
      override fun onSensorChanged(event: SensorEvent) {
        onEvent(event)
      }

      override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
      }
    }

    val sensorManager = systemService<SensorManager>()
    sensorManager.registerListener(listener, sensor, samplingRate.inWholeNanoseconds.toInt())
    onDispose { sensorManager.unregisterListener(listener) }
  }
}
