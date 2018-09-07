package com.ivianuu.essentials.util.ext

// todo inline fun <T, R> T.map(block: T.() -> R) = block(this)

inline fun <T, R> T.runIf(condition: Boolean, block: T.() -> R) = run {
    if (condition) {
        block()
    } else {
        null
    }
}

inline fun <T> T.applyIf(condition: Boolean, block: T.() -> Unit) = apply {
    if (condition) {
        block()
    }
}

inline fun <T> T.alsoIf(condition: Boolean, block: (T) -> Unit) = also {
    if (condition) {
        block(it)
    }
}

inline fun <T, R> T.letIf(condition: Boolean, block: (T) -> R) = let {
    if (condition) {
        block(it)
    } else {
        null
    }
}

inline fun <T> T.switch(block: T.() -> T) = run(block)

inline fun <T> T.switchIf(condition: Boolean, block: T.() -> T) = switch {
    if (condition) {
        block()
    } else {
        this
    }
}