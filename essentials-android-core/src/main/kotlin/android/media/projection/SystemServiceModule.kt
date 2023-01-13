package android.media.projection

import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun mediaProjectionManager(context: Context): MediaProjectionManager =
    context.getSystemService(MediaProjectionManager::class.java)
}
