package com.ivianuu.essentials.data.base

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.ivianuu.essentials.util.asMainCoroutineScope
import com.ivianuu.injectors.android.inject
import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.Scope

/**
 * Base service
 */
abstract class EsService : Service() {

    val scope: Scope get() = _scope
    private val _scope = MutableScope()

    val coroutineScope = scope.asMainCoroutineScope()

    override fun onCreate() {
        inject()
        super.onCreate()
    }

    override fun onDestroy() {
        _scope.close()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? = null
}