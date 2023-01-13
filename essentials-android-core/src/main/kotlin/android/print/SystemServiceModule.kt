package android.print

import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun printManager(context: Context): PrintManager =
    context.getSystemService(PrintManager::class.java)
}
