package com.ivianuu.essentials.util

import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.alias
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.scoped
import kotlinx.coroutines.Dispatchers

@Module
fun esUtilModule() {
    installIn<ApplicationComponent>()
    scoped {
        AppCoroutineDispatchers(
            io = Dispatchers.IO,
            computation = Dispatchers.Default,
            main = Dispatchers.Main
        )
    }
}

@Module
fun esLoggerModule() {
    alias<DefaultLogger, Logger>()
}
