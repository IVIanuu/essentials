package com.ivianuu.essentials

import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.ComponentElement
import com.ivianuu.injekt.common.ComponentName
import com.ivianuu.injekt.common.Disposable
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.common.TypeKey

interface ComponentStorage<N : ComponentName> {
  operator fun <T> get(key: Any): T?

  operator fun <T> set(key: Any, value: T)

  fun remove(key: Any)
}

inline fun <T> ComponentStorage<*>.scoped(key: Any, computation: () -> T): T {
  synchronized(this) {
    get<T>(key)?.let { return it }
    val value = computation()
    set(key, value)
    return value
  }
}

inline fun <T> ComponentStorage<*>.scoped(@Inject key: TypeKey<T>, computation: () -> T): T =
  scoped(key = key.value, computation = computation)

inline fun <T> ComponentStorage<*>.scoped(
  key: Any,
  vararg args: Any?,
  computation: () -> T
): T {
  val holder = scoped(key) { ScopedValueHolder() }

  synchronized(holder) {
    if (!args.contentEquals(holder.args)) {
      (holder.value as? Disposable)?.dispose()
      holder.value = computation()
      holder.args = args
    }
  }

  return holder.value as T
}

@Provide @ComponentElement<N> @Scoped<N>
class ComponentStorageImpl<N : ComponentName> : ComponentStorage<N>, Disposable {
  private val values = mutableMapOf<Any, Any?>()

  override operator fun <T> get(key: Any): T? = values[key] as? T

  override operator fun <T> set(key: Any, value: T) {
    values[key] = value
  }

  override fun remove(key: Any) {
    values.remove(key)
      ?.safeAs<Disposable>()
      ?.dispose()
  }

  override fun dispose() {
    val valuesToDispose = values.values.filterIsInstance<Disposable>()
    values.clear()
    valuesToDispose.forEach { it.dispose() }
  }
}

@PublishedApi internal class ScopedValueHolder : Disposable {
  var value: Any? = null
  var args: Array<out Any?>? = null

  override fun dispose() {
    args = null
    (value as? Disposable)?.dispose()
    value = null
  }
}
