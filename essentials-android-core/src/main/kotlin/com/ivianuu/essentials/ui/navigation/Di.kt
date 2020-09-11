package com.ivianuu.essentials.ui.navigation

import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.ApplicationContext

object EsNavigatorGivens {

    @Given(ApplicationContext::class)
    fun navigator() = Navigator()

}
