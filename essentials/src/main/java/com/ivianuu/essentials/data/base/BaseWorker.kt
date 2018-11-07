package com.ivianuu.essentials.data.base

import android.content.Context
import androidx.work.Worker
import com.ivianuu.essentials.util.ContextAware
import com.ivianuu.essentials.util.asMainCoroutineScope
import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.Scope

/**
 * Base worker
 */
abstract class BaseWorker : Worker(), ContextAware {

    override val providedContext: Context
        get() = applicationContext

    val scope: Scope get() = _scope
    private val _scope = MutableScope()

    val coroutineScope = scope.asMainCoroutineScope()

    override fun onStopped(cancelled: Boolean) {
        _scope.close()
        super.onStopped(cancelled)
    }
}