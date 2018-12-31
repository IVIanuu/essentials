package com.ivianuu.essentials.injection

import android.app.Application
import android.content.Context
import com.ivianuu.injekt.ComponentContext
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.get
import com.ivianuu.injekt.single

fun ComponentContext.application() = get<Application>()
fun ComponentContext.context() = get<Context>()

fun Module.sharedPreferences(
    sharedPreferencesName: String,
    sharedPreferencesMode: Int = Context.MODE_PRIVATE,
    name: String? = null,
    override: Boolean = false,
    createOnStart: Boolean = false
) = single(name, override, createOnStart) {
    context().getSharedPreferences(sharedPreferencesName, sharedPreferencesMode)!!
}