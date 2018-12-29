package com.ivianuu.essentials.app

import com.ivianuu.injekt.*
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

fun esAppServicesModule() = module {
    multiBindingMap<KClass<out AppService>, AppService>(APP_SERVICES)
}