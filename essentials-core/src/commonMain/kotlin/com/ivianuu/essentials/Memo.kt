package com.ivianuu.essentials

import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.common.SourceKey
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.scope.Disposable
import com.ivianuu.injekt.scope.Scope
import com.ivianuu.injekt.scope.scoped
import com.ivianuu.injekt.scope.withLock

inline fun <T> memo(
  key: Any,
  vararg args: Any?,
  @Inject scope: Scope,
  block: () -> T
): T {
  val holder = scoped(key) { MemoHolder() }

  return scope.withLock {
    if (holder.args == null || !holder.args!!.contentEquals(args)) {
      holder.value?.safeAs<Disposable>()?.dispose()
      holder.value = block()
      holder.args = args
    }

    holder.value
  } as T
}

inline fun <T> memoPositional(
  vararg args: Any?,
  @Inject key: SourceKey,
  @Inject scope: Scope,
  block: () -> T
): T = memo(key, *args, block = block)

inline fun <T> memoTyped(
  vararg args: Any?,
  @Inject key: TypeKey<T>,
  @Inject scope: Scope,
  block: () -> T
): T = memo(key, *args, block = block)

@PublishedApi internal class MemoHolder : Disposable {
  var args: Array<out Any?>? = null
  var value: Any? = null
  override fun dispose() {
    value?.safeAs<Disposable>()?.dispose()
  }
}

data class JoinedKey(val left: Any?, val right: Any?)

operator fun JoinedKey.plus(other: Any?): JoinedKey = JoinedKey(this, other)
