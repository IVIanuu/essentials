package essentials

import android.content.Context
import injekt.*
import kotlin.reflect.*

@Tag @Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
annotation class SystemService {
  @Provide companion object {
    @Provide fun <T : Any> systemService(clazz: KClass<T>, context: Context): @SystemService T =
      context.getSystemService(clazz.java)!!
  }
}
