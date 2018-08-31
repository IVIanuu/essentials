package com.ivianuu.essentials.util.ext

// todo inline fun <T, R> T.switch(block: T.() -> R) = block(this)

inline fun <T, R> T.runIf(condition: Boolean, block: T.() -> R): R? = run {
    if (condition) {
        block()
    } else null
}

inline fun <T> T.applyIf(condition: Boolean, block: T.() -> Unit): T = apply {
    if (condition) {
        block()
    }
}

inline fun <T> T.alsoIf(condition: Boolean, block: (T) -> Unit): T = also {
    if (condition) {
        block(it)
    }
}

inline fun <T, R> T.letIf(condition: Boolean, block: (T) -> R): R? = let {
    if (condition) {
        block(it)
    } else null
}