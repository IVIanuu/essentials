package com.ivianuu.essentials.util

import android.os.*
import com.ivianuu.essentials.*
import com.ivianuu.injekt.*
import kotlin.time.*
import android.os.Vibrator as AndroidVibrator

@Provide class Vibrator(private val androidVibrator: @SystemService AndroidVibrator) {
  suspend fun invoke(duration: Duration, amplitude: Float) {
    androidVibrator.vibrate(
      VibrationEffect.createOneShot(duration.inWholeMilliseconds, (255 * amplitude).toInt())
    )
  }
}
