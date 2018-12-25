package com.ivianuu.essentials.sample.ui

import android.app.Activity
import android.content.Context
import com.ivianuu.director.Controller
import com.ivianuu.director.application
import com.ivianuu.injekt.*
import kotlin.reflect.KClass

fun Controller.controllerComponent(
    modules: Iterable<Module> = emptyList(),
    dependsOn: Iterable<Component> = emptyList()
): Component {
    val deps = mutableSetOf<Component>()

    // application
    (application as? ComponentHolder)?.component?.let { deps.add(it) }

    // activity
    (activity as? ComponentHolder)?.component?.let { deps.add(it) }

    // parent
    (activity as? ComponentHolder)?.component?.let { deps.add(it) }

    deps.addAll(dependsOn)

    return component(
        modules = modules,
        dependsOn = deps
    )
}

fun Activity.activityComponent(
    modules: Iterable<Module> = emptyList(),
    dependsOn: Iterable<Component> = emptyList()
): Component {
    val deps = mutableSetOf<Component>()

    // application
    (application as? ComponentHolder)?.component?.let { deps.add(it) }

    deps.addAll(dependsOn)

    return component(
        modules = modules,
        dependsOn = deps
    )
}

inline fun <reified T : Activity> activityModule(
    activity: T,
    name: String? = null,
    noinline body: (Module.() -> Unit)? = null
) = activityModule(T::class, activity, name, body)

fun <T : Activity> activityModule(
    clazz: KClass<T>,
    activity: T,
    name: String? = null,
    body: (Module.() -> Unit)? = null
) = simpleModule(clazz, name) {
    single<Activity> { activity }
    single<Context> { activity }

    factory { activity.resources }

    // invoke the child
    body?.invoke(this)
}

inline fun <reified T : Any> simpleModule(
    instance: T,
    name: String? = null,
    noinline body: (Module.() -> Unit)? = null
) = simpleModule(T::class, instance, name, body)

fun <T : Any> simpleModule(
    clazz: KClass<T>,
    instance: T,
    name: String? = null,
    body: (Module.() -> Unit)? = null
) = module(name) {
    // bind instance
    single(clazz) { instance }
    body?.invoke(this)
}