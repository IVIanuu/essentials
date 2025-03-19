package essentials

import androidx.compose.runtime.*
import essentials.compose.*
import injekt.*
import kotlinx.coroutines.flow.*
import kotlin.reflect.*

inline fun <T> implicitly(x: T = inject): T = x

inline fun <A, R> provide(value: A, block: (@Provide A) -> R): R =
  block(value)

fun <T> nullOf(): T? = null

inline fun <reified T> Any?.cast(): T = this as T

@Suppress("UNCHECKED_CAST")
inline fun <T> Any?.unsafeCast(): T = this as T

inline fun <reified T> Any?.safeAs(): T? = this as? T

@Provide object CommonProviders {
  @Provide fun <K, V> pairsToMap(pairs: List<Pair<K, V>>) = pairs.toMap()

  @Provide inline fun <reified T : Any> kclass(): KClass<T> = T::class

  @Provide fun <T> (@Composable () -> T).asFlow(): Flow<T> =
    moleculeFlow { this() }
}
