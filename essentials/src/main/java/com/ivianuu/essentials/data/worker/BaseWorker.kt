package com.ivianuu.essentials.data.worker

import android.content.Context
import androidx.work.Worker
import com.ivianuu.essentials.util.ContextAware
import com.ivianuu.essentials.util.rx.DisposableScopeProvider

/**
 * Base worker
 */
abstract class BaseWorker : Worker(), ContextAware {

    override val providedContext: Context
        get() = applicationContext

    val scopeProvider = DisposableScopeProvider()

    override fun onStopped() {
        scopeProvider.dispose()
        super.onStopped()
    }

}