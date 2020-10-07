package com.ivianuu.essentials.ui

import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.android.ActivityComponent
import com.ivianuu.injekt.android.ActivityContext
import com.ivianuu.injekt.merge.MergeInto
import kotlinx.coroutines.CoroutineScope

typealias UiScope = CoroutineScope

@MergeInto(ActivityComponent::class)
@Module
object EsUiModule {

    @Binding(ActivityContext::class)
    fun uiScope(dispatchers: AppCoroutineDispatchers): UiScope =
        CoroutineScope(dispatchers.main)

}
