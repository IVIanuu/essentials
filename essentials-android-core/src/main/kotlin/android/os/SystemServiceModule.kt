package android.os

import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide fun vibrator(context: Context): Vibrator =
    context.getSystemService(Vibrator::class.java)

  @Provide fun dropBoxManager(context: Context): DropBoxManager =
    context.getSystemService(DropBoxManager::class.java)

  @Provide fun powerManager(context: Context): PowerManager =
    context.getSystemService(PowerManager::class.java)
}
