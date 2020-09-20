package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.channels.SendChannel

fun <E> SendChannel<E>.offerSafe(element: E): Boolean {
    return try {
        offer(element)
    } catch (t: Throwable) {
        false
    }
}