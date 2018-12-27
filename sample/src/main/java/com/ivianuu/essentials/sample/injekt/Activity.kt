package com.ivianuu.essentials.sample.injekt

import android.app.Activity
import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ComponentActivity
import androidx.fragment.app.FragmentActivity
import com.ivianuu.essentials.sample.ui.MainActivity
import com.ivianuu.injekt.*
import kotlin.reflect.KClass

fun Activity.activityComponent(
    modules: List<Module> = emptyList(),
    dependsOn: List<Component> = emptyList()
): Component {
    val deps = mutableListOf<Component>()

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
    noinline body: (Module.() -> Unit)? = null
) = activityModule(T::class, activity, body)

fun <T : Activity> activityModule(
    type: KClass<T>,
    activity: T,
    body: (Module.() -> Unit)? = null
) = module {
    factory(type) { activity }

    bind<MainActivity, AppCompatActivity>()
    bind<AppCompatActivity, FragmentActivity>()
    bind<FragmentActivity, ComponentActivity>()
    factory<Activity> { activity }

    factory<Application> { activity.application }

    factory { activity.resources }

    // invoke the child
    body?.invoke(this)
}