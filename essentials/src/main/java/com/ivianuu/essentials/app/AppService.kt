package com.ivianuu.essentials.app

import com.ivianuu.essentials.injection.multibinding.bindIntoClassMap
import com.ivianuu.essentials.injection.multibinding.classMapBinding
import com.ivianuu.essentials.injection.multibinding.intoClassMap
import com.ivianuu.injekt.Definition
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.module
import com.ivianuu.injekt.single
import com.ivianuu.scopes.MutableScope
import com.ivianuu.scopes.Scope

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
) = single(name, override, false, definition) intoClassMap APP_SERVICES

inline fun <reified T : AppService> Module.bindAppService() =
    bindIntoClassMap<AppService, T>(APP_SERVICES)

val esAppServicesModule = module {
    classMapBinding<AppService>(APP_SERVICES)
}