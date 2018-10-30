package com.ivianuu.essentials.data.base

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.Scope
import dagger.android.AndroidInjection
import kotlinx.coroutines.CoroutineScope

/**
 * Base service
 */
abstract class BaseService : Service(), CoroutineScope, Injectable {

    val scope: Scope get() = _scope
    private val _scope = MutableScope()

    override fun onCreate() {
        if (shouldInject) {
            AndroidInjection.inject(this)
        }
        super.onCreate()
    }

    override fun onDestroy() {
        _scope.close()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? = null
}