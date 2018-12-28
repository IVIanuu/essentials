package com.ivianuu.essentials.sample.injekt

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ComponentActivity
import androidx.fragment.app.FragmentActivity
import com.ivianuu.injekt.*
import kotlin.reflect.KClass

fun Activity.activityComponent(
    modules: List<Module> = emptyList(),
    dependencies: List<Component> = emptyList(),
    name: String? = null
): Component {
    val deps = mutableListOf<Component>()

    // application
    (application as? ComponentHolder)?.component?.let { deps.add(it) }

    deps.addAll(dependencies)

    return component(
        modules = modules,
        dependencies = dependencies,
        name = name
    )
}

inline fun <reified T : AppCompatActivity> activityModule(
    activity: T,
    name: String? = null,
    noinline definition: ModuleDefinition? = null
) = activityModule(T::class, activity, name, definition)

inline fun <reified T : AppCompatActivity> activityModule(
    type: KClass<T>,
    activity: T,
    name: String? = null,
    noinline definition: ModuleDefinition? = null
) = module(name = name) {
    factory(type) { activity }

    bind(type, AppCompatActivity::class)
    bind<AppCompatActivity, FragmentActivity>()
    bind<FragmentActivity, ComponentActivity>()
    bind<ComponentActivity, Activity>()
    bind<Activity, Context>()

    factory<Application> { activity.application }

    factory { activity.resources }

    definition?.invoke(this)
}