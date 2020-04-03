package com.ivianuu.essentials.util

import com.ivianuu.injekt.ApplicationScope
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.alias
import com.ivianuu.injekt.single
import kotlinx.coroutines.Dispatchers

@ApplicationScope
@Module
private fun ComponentBuilder.esUtilModule() {
    single {
        AppCoroutineDispatchers(
            io = Dispatchers.IO,
            computation = Dispatchers.Default,
            main = Dispatchers.Main
        )
    }

    alias<DefaultLogger, Logger>()
}
