package com.ivianuu.essentials.util

import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.single
import kotlinx.coroutines.Dispatchers

fun ComponentBuilder.esUtil() {
    single {
        AppCoroutineDispatchers(
            default = Dispatchers.Default,
            io = Dispatchers.IO,
            main = Dispatchers.Main,
            unconfined = Dispatchers.Unconfined
        )
    }
}