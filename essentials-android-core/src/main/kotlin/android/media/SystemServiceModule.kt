package android.media
import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun audioManager(context: Context): AudioManager =
    context.getSystemService(AudioManager::class.java)
}
