package com.ivianuu.essentials.injection

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import com.ivianuu.director.Controller
import com.ivianuu.injekt.Component
import com.ivianuu.injekt.ComponentHolder

fun Application.getComponentDependencies() = emptyList<Component>()

fun Activity.getComponentDependencies(): List<Component> {
    val deps = mutableListOf<Component>()
    deps.add(globalComponent)
    return deps
}

fun BroadcastReceiver.getComponentDependencies(context: Context): List<Component> {
    val deps = mutableListOf<Component>()
    deps.add(globalComponent)
    return deps
}

fun Controller.getComponentDependencies(): List<Component> {
    val deps = mutableListOf<Component>()

    if (parentController is ComponentHolder) {
        (parentController as? ComponentHolder)?.component?.let { deps.add(it) }
    } else {
        (activity as? ComponentHolder)?.component?.let { deps.add(it) }
    }

    return deps
}

fun Service.getComponentDependencies(): List<Component> {
    val deps = mutableListOf<Component>()
    deps.add(globalComponent)
    return deps
}