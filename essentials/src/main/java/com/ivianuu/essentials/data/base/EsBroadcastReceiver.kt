package com.ivianuu.essentials.data.base


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.InjektTrait
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.android.receiverComponent

/**
 * Base broadcast receiver
 */
abstract class EsBroadcastReceiver : BroadcastReceiver(), InjektTrait {

    override lateinit var component: Component

    override fun onReceive(context: Context, intent: Intent) {
        component = receiverComponent(this, context) {
            modules(this@EsBroadcastReceiver.modules(context))
        }
    }

    protected open fun modules(context: Context): List<Module> = emptyList()

}