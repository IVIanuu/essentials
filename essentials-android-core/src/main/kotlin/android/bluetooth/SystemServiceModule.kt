package android.bluetooth

import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun bluetoothManager(context: Context): BluetoothManager =
    context.getSystemService(BluetoothManager::class.java)
}
