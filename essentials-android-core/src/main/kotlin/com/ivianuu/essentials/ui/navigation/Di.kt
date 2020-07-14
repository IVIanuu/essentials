package com.ivianuu.essentials.ui.navigation

import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader

object EsNavigatorModule {

    @Given(ApplicationComponent::class)
    @Reader
    fun navigator() = Navigator()

}
