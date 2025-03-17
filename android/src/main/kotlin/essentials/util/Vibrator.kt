package essentials.util

import android.annotation.*
import android.app.*
import android.os.*
import androidx.core.content.*
import injekt.*
import kotlin.time.*
import android.os.Vibrator as AndroidVibrator

@Provide class Vibrator(private val context: Application) {
  @SuppressLint("MissingPermission")
  suspend fun vibrate(duration: Duration, amplitude: Float) {
    context.getSystemService<AndroidVibrator>()!!.vibrate(
      VibrationEffect.createOneShot(duration.inWholeMilliseconds, (255 * amplitude).toInt())
    )
  }
}
