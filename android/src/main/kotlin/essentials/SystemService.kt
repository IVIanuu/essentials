package essentials

import android.content.*
import android.content.pm.PackageManager
import injekt.*
import kotlin.reflect.*

@Tag annotation class SystemService {
  @Provide companion object {
    @Provide fun <T : Any> systemService(clazz: KClass<T>, context: Context): @SystemService T =
      context.getSystemService(clazz.java)!!
  }
}

fun <T : Any> systemService(
  scope: Scope<*> = inject,
  key: KClass<T> = inject
): T = SystemService.systemService(key, appContext())

fun packageManager(scope: Scope<*> = inject): PackageManager = appContext()
  .packageManager
