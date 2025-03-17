package essentials.util

import android.annotation.*
import android.app.*
import android.content.*
import android.content.res.*
import android.os.*
import androidx.compose.runtime.*
import androidx.core.content.*
import injekt.*
import kotlin.time.*

@Provide @Composable fun configuration(context: Application = inject): Configuration =
  produceState(context.resources.configuration) {
    val callbacks = object : ComponentCallbacks2 {
      override fun onConfigurationChanged(newConfig: Configuration) {
        value = newConfig
      }

      override fun onLowMemory() {
      }

      override fun onTrimMemory(level: Int) {
      }
    }
    context.registerComponentCallbacks(callbacks)
    awaitDispose { context.unregisterComponentCallbacks(callbacks) }
  }.value

@Tag typealias IsPowerSaveMode = Boolean

@Provide @Composable fun isPowerSaveMode(context: Application = inject): IsPowerSaveMode =
  broadcastStateOf(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED) {
    context.getSystemService<PowerManager>()!!.isPowerSaveMode
  }

@SuppressLint("MissingPermission")
suspend fun vibrate(
  duration: Duration,
  amplitude: Float,
  context: Application = inject
) {
  context.getSystemService<Vibrator>()!!.vibrate(
    VibrationEffect.createOneShot(duration.inWholeMilliseconds, (255 * amplitude).toInt())
  )
}
