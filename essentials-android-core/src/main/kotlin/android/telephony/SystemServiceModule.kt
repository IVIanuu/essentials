package android.telephony
import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun telephonyManager(context: Context): TelephonyManager =
    context.getSystemService(TelephonyManager::class.java)
}
