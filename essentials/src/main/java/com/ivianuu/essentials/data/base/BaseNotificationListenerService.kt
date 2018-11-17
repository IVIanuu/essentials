package com.ivianuu.essentials.data.base

import android.service.notification.NotificationListenerService
import com.ivianuu.essentials.util.asMainCoroutineScope
import com.ivianuu.injectors.android.inject
import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.ReusableScope
import com.ivianuu.scopes.Scope

/**
 * Base notification listener service
 */
abstract class BaseNotificationListenerService : NotificationListenerService() {

    val scope: Scope get() = _scope
    private val _scope = MutableScope()

    val coroutineScope = scope.asMainCoroutineScope()

    val connectedScope: Scope get() = _connectedScope
    private val _connectedScope = ReusableScope()

    val connectedCoroutineScope get() = _connectedCoroutineScope
    private var _connectedCoroutineScope = _connectedScope.asMainCoroutineScope()

    override fun onCreate() {
        inject()
        super.onCreate()
    }

    override fun onDestroy() {
        _scope.close()
        super.onDestroy()
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        _connectedCoroutineScope = _connectedScope.asMainCoroutineScope()
    }

    override fun onListenerDisconnected() {
        _connectedScope.clear()
        super.onListenerDisconnected()
    }
}