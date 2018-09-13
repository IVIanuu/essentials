package com.ivianuu.essentials.util.lifecycle

import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.Main
import kotlin.coroutines.CoroutineContext

/**
 * Lifecycle aware coroutine scope
 */
class LifecycleCoroutineScope(
    private val owner: LifecycleOwner
) : CoroutineScope, LifecycleOwner by owner {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    val job = LifecycleJob(this)

}