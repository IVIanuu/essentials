package com.ivianuu.essentials.util

import android.os.VibrationEffect
import com.ivianuu.essentials.SystemService
import com.ivianuu.injekt.Provide
import kotlin.time.Duration
import android.os.Vibrator as AndroidVibrator

@Provide class Vibrator(private val androidVibrator: @SystemService AndroidVibrator) {
  suspend fun invoke(duration: Duration, amplitude: Float) {
    androidVibrator.vibrate(
      VibrationEffect.createOneShot(duration.inWholeMilliseconds, (255 * amplitude).toInt())
    )
  }
}
