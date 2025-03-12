package essentials

import injekt.*
import kotlin.reflect.*

@Provide object CommonProviders {
  @Provide fun <K, V> pairsToMap(pairs: List<Pair<K, V>>) = pairs.toMap()

  @Provide inline fun <reified T : Any> kclass(): KClass<T> = T::class
}
