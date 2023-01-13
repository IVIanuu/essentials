package android.net

import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun connectivityManager(context: Context): ConnectivityManager =
    context.getSystemService(ConnectivityManager::class.java)
}
