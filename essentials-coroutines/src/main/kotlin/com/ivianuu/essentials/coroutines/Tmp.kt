package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.sync.Mutex

// todo ir
suspend fun <T> Mutex.withLockNoInline(owner: Any? = null, action: () -> T): T {
    lock(owner)
    try {
        return action()
    } finally {
        unlock(owner)
    }
}

// todo ir
fun <T> callbackFlowNoInline(@BuilderInference block: suspend (ProducerScope<T>) -> Unit): Flow<T> =
    channelFlow(block)