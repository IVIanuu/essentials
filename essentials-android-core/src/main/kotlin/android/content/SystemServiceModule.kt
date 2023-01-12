package android.content
import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide fun clipboardManager(context: Context): ClipboardManager =
    context.getSystemService(ClipboardManager::class.java)
}
