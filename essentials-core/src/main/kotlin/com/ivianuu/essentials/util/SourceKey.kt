package com.ivianuu.essentials.util

fun _sourceKey(): Any = error("Intrinsic")

inline fun sourceKeyOf(): Any = _sourceKey()

inline fun sourceKeyOf(p1: Any?): Any = JoinedKey(sourceKeyOf(), p1)

inline fun sourceKeyOf(p1: Any?, p2: Any?): Any =
    JoinedKey(JoinedKey(sourceKeyOf(), p1), p2)

inline fun sourceKeyOf(vararg inputs: Any?): Any =
    inputs.fold(sourceKeyOf()) { left, right -> JoinedKey(left, right) }

@PublishedApi
internal data class JoinedKey(val left: Any?, val right: Any?)
