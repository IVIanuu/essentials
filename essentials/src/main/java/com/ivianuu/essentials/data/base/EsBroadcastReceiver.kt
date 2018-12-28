package com.ivianuu.essentials.data.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ivianuu.essentials.injection.bindInstanceModule
import com.ivianuu.essentials.injection.componentName
import com.ivianuu.essentials.injection.getComponentDependencies
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.ComponentHolder
import com.ivianuu.injekt.component

/**
 * Base broadcast receiver
 */
abstract class EsBroadcastReceiver : BroadcastReceiver(), ComponentHolder {

    override lateinit var component: Component

    override fun onReceive(context: Context, intent: Intent) {
        component = component(
            modules = implicitModules() + modules(context, intent),
            dependencies = dependencies(context, intent),
            name = componentName()
        )
    }

    protected open fun dependencies(
        context: Context,
        intent: Intent
    ) = getComponentDependencies(context)

    protected open fun modules(
        context: Context,
        intent: Intent
    ) = listOf(bindInstanceModule(this))

    protected open fun implicitModules() = listOf(bindInstanceModule(this))
}