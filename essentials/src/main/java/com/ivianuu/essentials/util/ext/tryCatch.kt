package com.ivianuu.essentials.util.ext

inline fun <T> tryOrNull(block: () -> T): T? = try {
    block()
} catch (e: Exception) {
    null
}

inline fun <T> tryOrElse(defaultValue: T, block: () -> T): T = tryOrNull(block) ?: defaultValue