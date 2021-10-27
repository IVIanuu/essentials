package com.ivianuu.essentials

import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Component
import com.ivianuu.injekt.common.ComponentObserver
import com.ivianuu.injekt.common.Disposable
import com.ivianuu.injekt.common.EntryPoint
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.common.entryPoint
import com.ivianuu.injekt.common.synchronized

interface ComponentStorage<C : @Component Any> {
  operator fun <T> get(key: Any): T?

  operator fun <T> set(key: Any, value: T)

  fun remove(key: Any)
}

@EntryPoint<Any> interface ComponentStorageComponent<C : @Component Any> {
  val componentStorage: ComponentStorage<C>
}

val <C : @Component Any> C.componentStorage: ComponentStorage<C>
  get() = entryPoint<ComponentStorageComponent<C>>(this).componentStorage

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
      (holder.value as? ComponentObserver<*>)?.init()
    }
  }

  return holder.value as T
}

class ComponentStorageImpl<C : @Component Any> @Provide @Scoped<C> constructor() : ComponentStorage<C> {
  private val values = mutableMapOf<Any, Any?>()

  override operator fun <T> get(key: Any): T? = values[key] as? T

  override operator fun <T> set(key: Any, value: T) {
    values[key] = value
  }

  override fun remove(key: Any) {
    values.remove(key)
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
