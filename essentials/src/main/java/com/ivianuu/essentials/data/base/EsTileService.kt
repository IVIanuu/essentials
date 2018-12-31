package com.ivianuu.essentials.data.base

import android.annotation.TargetApi
import android.os.Build
import android.service.quicksettings.TileService
import com.ivianuu.essentials.injection.bindInstanceModule

import com.ivianuu.essentials.injection.getComponentDependencies
import com.ivianuu.essentials.injection.lazyComponent
import com.ivianuu.essentials.util.asMainCoroutineScope
import com.ivianuu.injekt.ComponentHolder
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.dependencies
import com.ivianuu.injekt.modules
import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.ReusableScope
import com.ivianuu.scopes.Scope

/**
 * Base tile service
 */
@TargetApi(Build.VERSION_CODES.N)
abstract class EsTileService : TileService(), ComponentHolder {

    override val component by lazyComponent {
        dependencies(this@EsTileService.dependencies())
        modules(implicitModules() + this@EsTileService.modules())
    }

    val scope: Scope get() = _scope
    private val _scope = MutableScope()

    val coroutineScope = scope.asMainCoroutineScope()

    val listeningScope: Scope get() = _listeningScope
    private val _listeningScope = ReusableScope()

    val listeningCoroutineScope get() = _listeningCoroutineScope
    private var _listeningCoroutineScope = _listeningScope.asMainCoroutineScope()

    override fun onDestroy() {
        _scope.close()
        super.onDestroy()
    }

    override fun onStartListening() {
        super.onStartListening()
        _listeningCoroutineScope = _listeningScope.asMainCoroutineScope()
    }

    override fun onStopListening() {
        _listeningScope.clear()
        super.onStopListening()
    }

    protected open fun dependencies() = getComponentDependencies()

    protected open fun modules() = emptyList<Module>()

    protected open fun implicitModules() = listOf(bindInstanceModule(this))

}