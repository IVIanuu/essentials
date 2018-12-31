package com.ivianuu.essentials.data.base


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ivianuu.essentials.injection.receiverComponent
import com.ivianuu.injekt.*

/**
 * Base broadcast receiver
 */
abstract class EsBroadcastReceiver : BroadcastReceiver(), ComponentHolder {

    override lateinit var component: Component

    override fun onReceive(context: Context, intent: Intent) {
        component = receiverComponent(context) {
            dependencies(this@EsBroadcastReceiver.dependencies(context))
            modules(this@EsBroadcastReceiver.modules(context))
        }
    }

    protected open fun dependencies(context: Context) = emptyList<Component>()

    protected open fun modules(context: Context) = emptyList<Module>()

}