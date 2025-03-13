package essentials.util

import android.annotation.*
import android.os.*
import essentials.*
import injekt.*
import kotlin.time.*
import android.os.Vibrator as AndroidVibrator

@Provide class Vibrator(private val androidVibrator: @SystemService AndroidVibrator) {
  @SuppressLint("MissingPermission")
  suspend fun vibrate(duration: Duration, amplitude: Float) {
    androidVibrator.vibrate(
      VibrationEffect.createOneShot(duration.inWholeMilliseconds, (255 * amplitude).toInt())
    )
  }
}
