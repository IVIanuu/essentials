package android.hardware.camera2
import android.content.Context
import com.ivianuu.injekt.Provide

object SystemServiceModule {
  @Provide inline fun cameraManager(context: Context): CameraManager =
    context.getSystemService(CameraManager::class.java)
}
