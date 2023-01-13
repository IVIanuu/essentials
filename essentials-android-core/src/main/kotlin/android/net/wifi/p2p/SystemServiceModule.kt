package android.net.wifi.p2p

import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun wifiP2pManager(context: Context): WifiP2pManager =
    context.getSystemService(WifiP2pManager::class.java)
}
