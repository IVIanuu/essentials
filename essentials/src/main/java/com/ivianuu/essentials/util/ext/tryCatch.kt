package com.ivianuu.essentials.util.ext

inline fun <T> tryOrNull(block: () -> T) = try {
    block()
} catch (e: Exception) {
    null
}

inline fun <T> tryOrDefault(defaultValue: T, block: () -> T) = tryOrNull(block) ?: defaultValue