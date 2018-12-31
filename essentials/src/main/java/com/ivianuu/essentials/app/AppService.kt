package com.ivianuu.essentials.app

import com.ivianuu.essentials.injection.bindIntoMap
import com.ivianuu.essentials.injection.intoMap
import com.ivianuu.essentials.injection.mapBinding
import com.ivianuu.injekt.Definition
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.module
import com.ivianuu.injekt.single
import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.Scope
import kotlin.reflect.KClass

const val APP_SERVICES = "appServices"

/**
 * Will be started on app start up and lifes as long as the app lives
 */
abstract class AppService {

    val scope: Scope get() = _scope
    private val _scope = MutableScope()

    open fun start() {
    }

}

inline fun <reified T : AppService> Module.appService(
    name: String? = null,
    override: Boolean = false,
    noinline definition: Definition<T>
) = single(name, override, false, definition) intoMap (APP_SERVICES to T::class)

inline fun <reified T : AppService> Module.bindAppService() =
    bindIntoMap<KClass<out AppService>, AppService, T>(APP_SERVICES, T::class)

val esAppServicesModule = module {
    mapBinding<KClass<out AppService>, AppService>(APP_SERVICES)
}