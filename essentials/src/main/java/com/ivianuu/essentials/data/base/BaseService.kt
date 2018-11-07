package com.ivianuu.essentials.data.base

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.ivianuu.essentials.util.asMainCoroutineScope
import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.Scope
import dagger.android.AndroidInjection

/**
 * Base service
 */
abstract class BaseService : Service() {

    val scope: Scope get() = _scope
    private val _scope = MutableScope()

    val coroutineScope = scope.asMainCoroutineScope()

    override fun onCreate() {
        AndroidInjection.inject(this)
        super.onCreate()
    }

    override fun onDestroy() {
        _scope.close()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? = null
}