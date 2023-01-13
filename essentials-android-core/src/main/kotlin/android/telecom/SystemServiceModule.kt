package android.telecom

import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun telecomManager(context: Context): TelecomManager =
    context.getSystemService(TelecomManager::class.java)
}
