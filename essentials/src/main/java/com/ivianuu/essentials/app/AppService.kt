package com.ivianuu.essentials.app

import com.ivianuu.injekt.BeanDefinition
import com.ivianuu.injekt.Definition
import com.ivianuu.injekt.ModuleContext
import com.ivianuu.injekt.module
import com.ivianuu.injekt.multibinding.bindIntoClassMap
import com.ivianuu.injekt.multibinding.classMapBinding
import com.ivianuu.injekt.multibinding.intoClassMap
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

inline fun <reified T : AppService> ModuleContext.appService(
    name: String? = null,
    override: Boolean = false,
    noinline definition: Definition<T>
): BeanDefinition<T> = single(name, override, false, definition) intoClassMap APP_SERVICES

inline fun <reified T : AppService> ModuleContext.bindAppService(): BeanDefinition<AppService> =
    bindIntoClassMap<AppService, T>(APP_SERVICES)

val esAppServicesModule = module("EsAppServicesModule") {
    classMapBinding<AppService>(APP_SERVICES)
}