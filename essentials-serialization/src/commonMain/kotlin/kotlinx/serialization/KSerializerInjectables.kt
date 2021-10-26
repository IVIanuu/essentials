package kotlinx.serialization

import com.ivianuu.essentials.cast
import com.ivianuu.injekt.Provide
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@Provide fun <T> kSerializer(type: KTypeT<T>): KSerializer<T> =
  serializer(type).cast()

typealias KTypeT<T> = KType

@Provide inline fun <reified T> kTypeT(): KTypeT<T> = typeOf<T>()
