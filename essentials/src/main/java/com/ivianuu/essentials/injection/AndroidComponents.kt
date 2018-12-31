package com.ivianuu.essentials.injection

import android.app.Activity
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import com.ivianuu.director.Controller
import com.ivianuu.injekt.ComponentDefinition
import com.ivianuu.injekt.ComponentHolder
import com.ivianuu.injekt.component

fun Activity.activityComponent(
    createEagerInstances: Boolean = true,
    definition: ComponentDefinition? = null
) = component(createEagerInstances) {
    (application as? ComponentHolder)?.let { dependencies(it.component) }
    definition?.invoke(this)
}

fun BroadcastReceiver.receiverComponent(
    context: Context,
    createEagerInstances: Boolean = true,
    definition: ComponentDefinition? = null
) = component(createEagerInstances) {
    (context.applicationContext as? ComponentHolder)?.let { dependencies(it.component) }
    definition?.invoke(this)
}

fun Controller.controllerComponent(
    createEagerInstances: Boolean = true,
    definition: ComponentDefinition? = null
) = component(createEagerInstances) {
    if (parentController is ComponentHolder) {
        (parentController as? ComponentHolder)?.component?.let { dependencies(it) }
    } else {
        (activity as? ComponentHolder)?.component?.let { dependencies(it) }
    }
    definition?.invoke(this)
}

fun Service.serviceComponent(
    createEagerInstances: Boolean = true,
    definition: ComponentDefinition? = null
) = component(createEagerInstances) {
    (application as? ComponentHolder)?.let { dependencies(it.component) }
    definition?.invoke(this)
}