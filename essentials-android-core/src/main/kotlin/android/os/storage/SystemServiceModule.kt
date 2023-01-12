package android.os.storage
import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun storageManager(context: Context): StorageManager =
    context.getSystemService(StorageManager::class.java)
}
