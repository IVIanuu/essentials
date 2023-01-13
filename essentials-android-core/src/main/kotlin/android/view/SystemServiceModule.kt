package android.view

import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun layoutInflater(context: Context): LayoutInflater =
    context.getSystemService(LayoutInflater::class.java)
  @Provide inline fun windowManager(context: Context): WindowManager =
    context.getSystemService(WindowManager::class.java)
}
