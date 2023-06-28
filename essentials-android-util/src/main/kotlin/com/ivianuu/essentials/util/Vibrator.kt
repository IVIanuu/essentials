package com.ivianuu.essentials.util

import android.os.VibrationEffect
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.SystemService
import kotlin.time.Duration
import android.os.Vibrator as AndroidVibrator

fun interface Vibrator {
  suspend operator fun invoke(duration: Duration) = invoke(duration, 1f)

  suspend operator fun invoke(duration: Duration, amplitude: Float)
}

@Provide class VibratorImpl(private val vibrator: @SystemService AndroidVibrator) : Vibrator {
  override suspend fun invoke(duration: Duration, amplitude: Float) {
    vibrator.vibrate(
      VibrationEffect.createOneShot(duration.inWholeMilliseconds, (255 * amplitude).toInt())
    )
  }
}
