package android.media
import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide fun audioManager(context: Context): AudioManager =
    context.getSystemService(AudioManager::class.java)
}
