package com.ivianuu.essentials.data.worker

import android.content.Context
import androidx.work.Worker
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.util.ContextAware
import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.Scope
import com.ivianuu.scopes.coroutines.cancelBy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.android.Main
import kotlin.coroutines.CoroutineContext

/**
 * Base worker
 */
abstract class BaseWorker : Worker(), ContextAware, CoroutineScope,
    Injectable {

    override val providedContext: Context
        get() = applicationContext

    val scope: Scope get() = _scope
    private val _scope = MutableScope()

    val job = Job().cancelBy(scope)

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onStopped(cancelled: Boolean) {
        _scope.close()
        super.onStopped(cancelled)
    }
}