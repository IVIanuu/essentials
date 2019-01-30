package com.ivianuu.essentials.app

import com.ivianuu.essentials.util.asMainCoroutineScope
import com.ivianuu.injekt.BindingContext
import com.ivianuu.injekt.Definition
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.module
import com.ivianuu.injekt.multibinding.bindIntoMap
import com.ivianuu.injekt.multibinding.mapBinding
import com.ivianuu.injekt.single
import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.Scope

const val APP_SERVICES = "appServices"

/**
 * Will be started on app start up and lives as long as the app lives
 */
abstract class AppService {

    val scope: Scope get() = _scope
    private val _scope = MutableScope()

    val coroutineScope = scope.asMainCoroutineScope()

    open fun start() {
    }

}

inline fun <reified T : AppService> Module.appService(
    name: String? = null,
    override: Boolean = false,
    noinline definition: Definition<T>
): BindingContext<T> =
    single(name, null, override, false, definition) bindIntoMap (APP_SERVICES to T::class)

inline fun <reified T : AppService> Module.bindAppService(name: String? = null) {
    bindIntoMap<T>(APP_SERVICES, T::class, implementationType = T::class, implementationName = name)
}

val esAppServicesModule = module("EsAppServicesModule") {
    mapBinding(APP_SERVICES)
}