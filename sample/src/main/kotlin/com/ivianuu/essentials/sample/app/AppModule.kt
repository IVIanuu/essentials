package com.ivianuu.essentials.sample.app

import com.ivianuu.essentials.app.bindAppInitializerIntoMap
import com.ivianuu.essentials.sample.PurchaseInitializer
import com.ivianuu.injekt.ApplicationScope
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Module

@ApplicationScope
@Module
private fun ComponentBuilder.appModule() {
    bindAppInitializerIntoMap<PurchaseInitializer>()
}
