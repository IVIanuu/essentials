package android.net.wifi
import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun wifiManager(context: Context): WifiManager =
    context.getSystemService(WifiManager::class.java)
}
