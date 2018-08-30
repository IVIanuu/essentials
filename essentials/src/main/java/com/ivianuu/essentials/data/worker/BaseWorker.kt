package com.ivianuu.essentials.data.worker

import android.content.Context
import androidx.work.Worker
import com.ivianuu.essentials.util.ContextAware
import com.ivianuu.essentials.util.rx.disposableScopeProvider

/**
 * Base worker
 */
abstract class BaseWorker : Worker(), ContextAware {

    override val providedContext: Context
        get() = applicationContext

    val scopeProvider = disposableScopeProvider()

    override fun onStopped(cancelled: Boolean) {
        scopeProvider.dispose()
        super.onStopped(cancelled)
    }
}