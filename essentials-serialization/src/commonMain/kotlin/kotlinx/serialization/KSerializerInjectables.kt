package kotlinx.serialization

import com.ivianuu.essentials.cast
import com.ivianuu.injekt.Provide
import kotlin.reflect.KTypeT

object SerializationInjectables {
  @Provide inline fun <T> kSerializer(type: KTypeT<T>): KSerializer<T> =
    serializer(type).cast()
}
