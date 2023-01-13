package android.net.nsd

import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun nsdManager(context: Context): NsdManager =
    context.getSystemService(NsdManager::class.java)
}
