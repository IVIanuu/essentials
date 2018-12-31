package com.ivianuu.essentials.data.base

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.ivianuu.essentials.injection.bindInstanceModule

import com.ivianuu.essentials.injection.getComponentDependencies
import com.ivianuu.essentials.injection.lazyComponent
import com.ivianuu.essentials.util.asMainCoroutineScope
import com.ivianuu.injekt.*
import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.Scope

/**
 * Base service
 */
abstract class EsService : Service(), ComponentHolder {

    override val component by lazyComponent {
        dependencies(implicitDependencies() + this@EsService.dependencies())
        modules(implicitModules() + this@EsService.modules())
    }

    val scope: Scope get() = _scope
    private val _scope = MutableScope()

    val coroutineScope = scope.asMainCoroutineScope()

    override fun onDestroy() {
        _scope.close()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? = null

    protected open fun dependencies() = emptyList<Component>()

    protected open fun implicitDependencies() = getComponentDependencies()

    protected open fun modules() = emptyList<Module>()

    protected open fun implicitModules() = listOf(bindInstanceModule(this))

}