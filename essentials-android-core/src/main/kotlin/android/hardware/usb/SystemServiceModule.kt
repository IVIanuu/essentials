package android.hardware.usb

import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun usbManager(context: Context): UsbManager =
    context.getSystemService(UsbManager::class.java)
}
