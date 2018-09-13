package com.ivianuu.essentials.util.coroutines

import com.ivianuu.essentials.util.ext.delegate
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
}

@Suppress("FunctionName")
fun CancellableCoroutineScope(): CancellableCoroutineScope = CancellableCoroutineScopeImpl()

private class CancellableCoroutineScopeImpl : CancellableCoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    val job = Job()

    override fun cancel() {
        job.cancel()
    }
}

fun CoroutineScope.cancel() {
    delegate<CancellableCoroutineScopeImpl>().cancel()
}