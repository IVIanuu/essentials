package com.ivianuu.essentials.util

import android.os.*
import com.ivianuu.essentials.*
import injekt.*
import kotlin.time.*
import android.os.Vibrator as AndroidVibrator

@Provide class Vibrator(private val androidVibrator: @SystemService AndroidVibrator) {
  suspend fun vibrate(duration: Duration, amplitude: Float) {
    androidVibrator.vibrate(
      VibrationEffect.createOneShot(duration.inWholeMilliseconds, (255 * amplitude).toInt())
    )
  }
}
