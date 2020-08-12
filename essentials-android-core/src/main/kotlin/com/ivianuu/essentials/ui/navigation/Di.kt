package com.ivianuu.essentials.ui.navigation

import com.ivianuu.injekt.ApplicationStorage
import com.ivianuu.injekt.Given

object EsNavigatorModule {

    @Given(ApplicationStorage::class)
    fun navigator() = Navigator()

}
