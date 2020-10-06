package com.ivianuu.essentials.ui.navigation

import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.merge.ApplicationComponent
import com.ivianuu.injekt.merge.MergeInto

@MergeInto(ApplicationComponent::class)
@Module
object EsNavigatorModule {

    @Binding(ApplicationComponent::class)
    fun navigator() = Navigator()

}
