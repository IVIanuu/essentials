package com.ivianuu.essentials.ui.navigation

import com.ivianuu.injekt.ApplicationScoped
import com.ivianuu.injekt.Given

object EsNavigatorModule {

    @Given(ApplicationScoped::class)
    fun navigator() = Navigator()

}
