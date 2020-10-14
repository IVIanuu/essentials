package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

suspend inline fun runWithCleanup(
    crossinline cleanup: () -> Unit,
    block: () -> Unit,
) {
    try {
        block()
    } catch (e: CancellationException) {
    } finally {
        withContext(NonCancellable) {
            cleanup()
        }
    }
}
