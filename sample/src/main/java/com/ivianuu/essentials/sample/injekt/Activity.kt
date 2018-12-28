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
    dependencies: List<Component> = emptyList()
): Component {
    val deps = mutableListOf<Component>()

    // application
    (application as? ComponentHolder)?.component?.let { deps.add(it) }

    deps.addAll(dependencies)

    return component(
        modules = modules,
        dependencies = dependencies
    )
}

inline fun <reified T : AppCompatActivity> activityModule(
    activity: T,
    noinline definition: ModuleDefinition? = null
) = activityModule(T::class, activity, definition)

fun <T : AppCompatActivity> activityModule(
    type: KClass<T>,
    activity: T,
    definition: ModuleDefinition? = null
) = module {
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