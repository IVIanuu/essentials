package android.net.wifi
import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide fun wifiManager(context: Context): WifiManager =
    context.getSystemService(WifiManager::class.java)
}
