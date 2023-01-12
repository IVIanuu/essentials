package android.app.admin
import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun devicePolicyManager(context: Context): DevicePolicyManager =
    context.getSystemService(DevicePolicyManager::class.java)
}
