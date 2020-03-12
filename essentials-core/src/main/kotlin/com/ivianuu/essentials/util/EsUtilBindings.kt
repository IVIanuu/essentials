package com.ivianuu.essentials.util

import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.single
import kotlinx.coroutines.Dispatchers

fun ComponentBuilder.esUtil() {
    single {
        AppCoroutineDispatchers(
            io = Dispatchers.IO,
            computation = Dispatchers.Default,
            main = Dispatchers.Main
        )
    }
}
