package com.ivianuu.essentials.util

import android.annotation.SuppressLint
import android.os.VibrationEffect
import com.ivianuu.injekt.Provide
import kotlin.time.Duration
import android.os.Vibrator as AndroidVibrator

interface Vibrator {
  suspend operator fun invoke(duration: Duration, amplitude: Float = 1f)
}

@Provide class VibratorImpl(private val vibrator: AndroidVibrator) : Vibrator {
  @SuppressLint("MissingPermission")
  override suspend fun invoke(duration: Duration, amplitude: Float) {
    vibrator.vibrate(
      VibrationEffect.createOneShot(
        duration.inWholeMilliseconds,
        (255 * amplitude).toInt(),
      )
    )
  }
}
