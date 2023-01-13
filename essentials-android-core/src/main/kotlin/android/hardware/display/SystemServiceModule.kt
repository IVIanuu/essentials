package android.hardware.display

import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun displayManager(context: Context): DisplayManager =
    context.getSystemService(DisplayManager::class.java)
}
