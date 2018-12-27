package com.ivianuu.injekt

internal inline fun debug(msg: () -> String) {
    InjektPlugins.logger?.debug(msg())
}

internal inline fun info(msg: () -> String) {
    InjektPlugins.logger?.info(msg())
}

internal inline fun warn(msg: () -> String) {
    InjektPlugins.logger?.warn(msg())
}

internal inline fun error(msgAndThrowable: () -> Pair<String, Throwable?>) {
    InjektPlugins.logger?.let { logger ->
        val pair = msgAndThrowable()
        logger.error(pair.first, pair.second)
    }
}