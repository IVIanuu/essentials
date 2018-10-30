package com.ivianuu.essentials.data.base

import android.service.notification.NotificationListenerService
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.util.coroutines.ScopeCoroutineScope
import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.Scope
import com.ivianuu.scopes.coroutines.cancelBy
import dagger.android.AndroidInjection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.android.Main
import kotlin.coroutines.CoroutineContext

/**
 * Base notification listener service
 */
abstract class BaseNotificationListenerService : NotificationListenerService(),
    CoroutineScope, Injectable {

    val scope: Scope get() = _scope
    private val _scope = MutableScope()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    val job = Job().cancelBy(scope)

    val connectedScope: Scope get() = _connectedScope
    private var _connectedScope = MutableScope()

    val connectedCoroutineScope: CoroutineScope get() = _connectedCoroutineScope
    private var _connectedCoroutineScope = ScopeCoroutineScope(connectedScope)

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

    override fun onListenerConnected() {
        super.onListenerConnected()
        _connectedScope = MutableScope()
        _connectedCoroutineScope = ScopeCoroutineScope(_connectedScope)
    }

    override fun onListenerDisconnected() {
        _connectedScope.close()
        super.onListenerDisconnected()
    }
}