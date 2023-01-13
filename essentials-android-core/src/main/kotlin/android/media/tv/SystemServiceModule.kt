package android.media.tv

import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun tvInputManager(context: Context): TvInputManager =
    context.getSystemService(TvInputManager::class.java)
}
