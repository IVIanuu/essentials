package com.ivianuu.essentials.data.base

import android.service.notification.NotificationListenerService
import com.ivianuu.essentials.injection.Injectable
import com.ivianuu.essentials.util.coroutines.asMainCoroutineScope
import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.Scope
import dagger.android.AndroidInjection

/**
 * Base notification listener service
 */
abstract class BaseNotificationListenerService : NotificationListenerService(), Injectable {

    val scope: Scope get() = _scope
    private val _scope = MutableScope()

    val coroutineScope = scope.asMainCoroutineScope()

    val connectedScope: Scope get() = _connectedScope
    private var _connectedScope = MutableScope()

    val connectedCoroutineScope get() = _connectedCoroutineScope
    private var _connectedCoroutineScope = connectedScope.asMainCoroutineScope()

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
        _connectedCoroutineScope = _connectedScope.asMainCoroutineScope()
    }

    override fun onListenerDisconnected() {
        _connectedScope.close()
        super.onListenerDisconnected()
    }
}