package com.ivianuu.essentials.ui

import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Qualifier
import com.ivianuu.injekt.android.ActivityComponent
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.get
import com.ivianuu.injekt.scoped
import kotlinx.coroutines.CoroutineScope

@Target(AnnotationTarget.TYPE)
@Qualifier
annotation class UiScope

@Module
fun esUiModule() {
    installIn<ActivityComponent>()
    scoped<@UiScope CoroutineScope> {
        CoroutineScope(get<AppCoroutineDispatchers>().main)
    }
}