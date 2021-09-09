package kotlinx.serialization

import com.ivianuu.injekt.Provide
import kotlinx.serialization.json.Json

@Provide inline fun <reified T> kSerializer(json: Json): KSerializer<T> =
  serializer()
