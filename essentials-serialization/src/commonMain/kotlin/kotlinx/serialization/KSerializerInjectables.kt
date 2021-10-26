package kotlinx.serialization

import com.ivianuu.essentials.cast
import com.ivianuu.essentials.serialization.KTypeT
import com.ivianuu.injekt.Provide

@Provide fun <T> kSerializer(type: KTypeT<T>): KSerializer<T> =
  serializer(type).cast()
