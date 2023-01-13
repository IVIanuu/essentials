package android.hardware.input

import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun inputManager(context: Context): InputManager =
    context.getSystemService(InputManager::class.java)
}
