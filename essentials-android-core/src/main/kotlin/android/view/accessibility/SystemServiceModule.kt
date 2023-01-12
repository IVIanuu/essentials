package android.view.accessibility
import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun accessibilityManager(context: Context): AccessibilityManager =
    context.getSystemService(AccessibilityManager::class.java)
}
