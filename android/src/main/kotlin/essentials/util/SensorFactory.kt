package essentials.util

import android.hardware.*
import essentials.*
import essentials.compose.*
import injekt.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlin.time.*
import kotlin.time.Duration.Companion.nanoseconds
import android.hardware.SensorEvent as AndroidSensorEvent

@Provide class SensorFactory(
  private val sensorManager: @SystemService SensorManager,
  private val wakeLockManager: WakeLockManager,
) {
  fun sensor(type: Int): Sensor? = sensorManager.getDefaultSensor(type)

  fun sensorEvents(sensor: Sensor, samplingRate: Duration): Flow<SensorEvent> =
    callbackFlow {
      if (!sensor.isWakeUpSensor)
        launchMolecule { wakeLockManager.WakeLock("sensors") }

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

  fun sensorEvents(sensorType: Int, samplingRate: Duration): Flow<SensorEvent> =
    sensorEvents(sensor(sensorType)!!, samplingRate)
}

data class SensorEvent(
  var sensor: Sensor,
  var timestamp: Duration = Duration.ZERO,
  val values: FloatArray = floatArrayOf(),
  var accuracy: Int = 0
)
