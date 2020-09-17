package com.ivianuu.essentials.util

import com.ivianuu.injekt.ForKey
import com.ivianuu.injekt.Key
import com.ivianuu.injekt.keyOf

fun <@ForKey T> qualifiedKeyOf(vararg qualifiers: Any?): Key<T> {
    return Key(
        buildString {
            appendLine(keyOf<T>().value)
            if (qualifiers.isNotEmpty()) {
                appendLine("[")
                qualifiers.joinToString(",") { it.toString() }
                appendLine("]")
            }
        }
    )
}
