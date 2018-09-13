package com.ivianuu.essentials.util.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.android.Main
import kotlin.coroutines.CoroutineContext

/**
 * A coroutine scope which is cancelable
 */
interface CancellableCoroutineScope : CoroutineScope {
    fun cancel()

    companion object {
        operator fun invoke(): CancellableCoroutineScope =
            CancellableCoroutineScopeImpl()
    }
}

private class CancellableCoroutineScopeImpl : CancellableCoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    val job = Job()

    override fun cancel() {
        job.cancel()
    }
}