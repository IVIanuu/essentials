package com.ivianuu.injekt

internal inline fun debug(msg: () -> String) {
    Injekt.logger?.debug(msg())
}

internal inline fun info(msg: () -> String) {
    Injekt.logger?.info(msg())
}

internal inline fun warn(msg: () -> String) {
    Injekt.logger?.warn(msg())
}

internal inline fun error(msgAndThrowable: () -> Pair<String, Throwable?>) {
    Injekt.logger?.let { logger ->
        val pair = msgAndThrowable()
        logger.error(pair.first, pair.second)
    }
}
