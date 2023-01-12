package android.view.inputmethod
import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun inputMethodManager(context: Context): InputMethodManager =
    context.getSystemService(InputMethodManager::class.java)
}
