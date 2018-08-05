package com.ivianuu.essentials.data.worker

import android.content.Context
import androidx.work.Worker
import com.ivianuu.essentials.util.ContextAware

/**
 * Base worker
 */
abstract class BaseWorker : Worker(), ContextAware {

    override val providedContext: Context
        get() = applicationContext

}