package com.ivianuu.essentials.ui.navigation

import com.ivianuu.injekt.ApplicationContext
import com.ivianuu.injekt.Given

object EsNavigatorModule {

    @Given(ApplicationContext::class)
    fun navigator() = Navigator()

}
