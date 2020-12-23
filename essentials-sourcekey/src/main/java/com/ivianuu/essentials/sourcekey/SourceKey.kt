package com.ivianuu.essentials.sourcekey

import com.ivianuu.injekt.component.Storage
import com.ivianuu.injekt.component.memo

fun _sourceKey(): Any = error("Intrinsic")

inline fun sourceKeyOf(): Any = _sourceKey()

inline fun sourceKeyOf(p1: Any?): Any = JoinedKey(sourceKeyOf(), p1)

inline fun sourceKeyOf(p1: Any?, p2: Any?): Any =
    JoinedKey(JoinedKey(sourceKeyOf(), p1), p2)

inline fun sourceKeyOf(vararg inputs: Any?): Any =
    inputs.fold(sourceKeyOf()) { left, right -> JoinedKey(left, right) }

@PublishedApi
internal data class JoinedKey(val left: Any?, val right: Any?)

inline fun <T : Any> Storage<*>.memo(init: () -> T): T = memo(key = sourceKeyOf(), block = init)
