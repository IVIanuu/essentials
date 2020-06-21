package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

suspend fun runWithCleanup(
    cleanup: suspend () -> Unit,
    block: suspend () -> Unit
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
