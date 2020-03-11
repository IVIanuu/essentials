package com.ivianuu.essentials.util

import com.ivianuu.injekt.Module
import com.ivianuu.injekt.single
import kotlinx.coroutines.Dispatchers

val EsUtilModule = Module {
    single {
        AppCoroutineDispatchers(
            default = Dispatchers.Default,
            io = Dispatchers.IO,
            main = Dispatchers.Main,
            unconfined = Dispatchers.Unconfined
        )
    }
}