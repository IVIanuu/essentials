package com.ivianuu.essentials.sample.app

import com.ivianuu.essentials.app.bindAppInitializerIntoMap
import com.ivianuu.essentials.sample.PurchaseInitializer
import com.ivianuu.injekt.ComponentBuilder

fun ComponentBuilder.appBindings() {
    bindAppInitializerIntoMap<PurchaseInitializer>()
}
