package com.ivianuu.essentials.data.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ivianuu.essentials.injection.bindInstanceModule

import com.ivianuu.essentials.injection.getComponentDependencies
import com.ivianuu.injekt.*

/**
 * Base broadcast receiver
 */
abstract class EsBroadcastReceiver : BroadcastReceiver(), ComponentHolder {

    override lateinit var component: Component

    override fun onReceive(context: Context, intent: Intent) {
        component = component {
            dependencies(
                implicitDependencies(context) + this@EsBroadcastReceiver.dependencies(
                    context
                )
            )
            modules(implicitModules(context) + this@EsBroadcastReceiver.modules(context))
        }
    }

    protected open fun dependencies(context: Context) = emptyList<Component>()

    protected open fun implicitDependencies(context: Context) = getComponentDependencies(context)

    protected open fun modules(context: Context) = emptyList<Module>()

    protected open fun implicitModules(context: Context) = listOf(bindInstanceModule(this))
}