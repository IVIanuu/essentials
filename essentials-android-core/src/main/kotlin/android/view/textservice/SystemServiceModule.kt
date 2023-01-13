package android.view.textservice

import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun textServicesManager(context: Context): TextServicesManager =
    context.getSystemService(TextServicesManager::class.java)
}
