package android.location
import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun locationManager(context: Context): LocationManager =
    context.getSystemService(LocationManager::class.java)
}
