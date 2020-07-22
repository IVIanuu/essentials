package com.ivianuu.essentials.ui.navigation

import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Given

object EsNavigatorModule {

    @Given(ApplicationComponent::class)
    fun navigator() = Navigator()

}
