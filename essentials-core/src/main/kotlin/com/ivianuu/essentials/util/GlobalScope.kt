package com.ivianuu.essentials.util

import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.merge.ApplicationComponent
import com.ivianuu.injekt.merge.MergeInto
import kotlinx.coroutines.CoroutineScope

typealias GlobalScope = CoroutineScope

@MergeInto(ApplicationComponent::class)
@Module
object EsGlobalScopeModule {

    @Binding(ApplicationComponent::class)
    fun globalScope(dispatchers: AppCoroutineDispatchers): GlobalScope =
        CoroutineScope(dispatchers.default)

}
