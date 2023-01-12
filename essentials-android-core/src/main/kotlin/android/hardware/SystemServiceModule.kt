package android.hardware
import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide fun sensorManager(context: Context): SensorManager =
    context.getSystemService(SensorManager::class.java)
}
