package com.ivianuu.essentials.data.base

import android.service.notification.NotificationListenerService
import com.ivianuu.essentials.util.asMainCoroutineScope
import com.ivianuu.essentials.util.ext.unsafeLazy
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.android.serviceComponent
import com.ivianuu.injekt.modules
import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.ReusableScope
import com.ivianuu.scopes.Scope

/**
 * Base notification listener service
 */
abstract class EsNotificationListenerService : NotificationListenerService(), InjektTrait {

    override val component by unsafeLazy {
        serviceComponent(this) {
            modules(this@EsNotificationListenerService.modules())
        }
    }

    val scope: Scope get() = _scope
    private val _scope = MutableScope()

    val coroutineScope = scope.asMainCoroutineScope()

    val connectedScope: Scope get() = _connectedScope
    private val _connectedScope = ReusableScope()

    val connectedCoroutineScope get() = _connectedCoroutineScope
    private var _connectedCoroutineScope = _connectedScope.asMainCoroutineScope()

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

    protected open fun modules() = emptyList<Module>()

}