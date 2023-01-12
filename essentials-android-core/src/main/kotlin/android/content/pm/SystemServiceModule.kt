package android.content.pm
import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide fun packageManager(context: Context): PackageManager =
    context.getSystemService(PackageManager::class.java)
}
