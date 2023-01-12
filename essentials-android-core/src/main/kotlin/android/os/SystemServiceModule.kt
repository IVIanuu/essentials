package android.os
import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun dropBoxManager(context: Context): DropBoxManager =
    context.getSystemService(DropBoxManager::class.java)

  @Provide inline fun powerManager(context: Context): PowerManager =
    context.getSystemService(PowerManager::class.java)

  @Provide inline fun vibrator(context: Context): Vibrator =
    context.getSystemService(Vibrator::class.java)
}
