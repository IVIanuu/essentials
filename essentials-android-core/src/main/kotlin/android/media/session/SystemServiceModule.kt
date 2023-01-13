package android.media.session

import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun mediaSessionManager(context: Context): MediaSessionManager =
    context.getSystemService(MediaSessionManager::class.java)
}
