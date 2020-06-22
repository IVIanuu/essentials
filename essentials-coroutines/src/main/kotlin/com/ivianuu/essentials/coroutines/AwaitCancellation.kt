package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.suspendCancellableCoroutine

suspend fun awaitCancellation() = suspendCancellableCoroutine<Unit> { }
