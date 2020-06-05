package com.ivianuu.essentials.ui.base

import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.android.ActivityComponent
import com.ivianuu.injekt.android.ForActivity
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.scoped
import kotlinx.coroutines.CoroutineScope

@Module
fun esActivityModule() {
    installIn<ActivityComponent>()
    scoped { coroutineScope: @ForActivity CoroutineScope,
             dispatchers: AppCoroutineDispatchers, logger: Logger ->
        Navigator(
            coroutineScope = coroutineScope,
            dispatchers = dispatchers,
            logger = logger
        )
    }
}
