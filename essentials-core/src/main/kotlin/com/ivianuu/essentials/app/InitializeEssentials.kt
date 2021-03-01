package com.ivianuu.essentials.app

import com.ivianuu.injekt.Given
import com.ivianuu.injekt.component.App
import com.ivianuu.injekt.component.AppComponent
import com.ivianuu.injekt.component.ComponentElement
import com.ivianuu.injekt.component.ComponentInitializer
import com.ivianuu.injekt.component.ComponentInitializerBinding
import com.ivianuu.injekt.component.initializeApp

fun App.initializeEssentials(
    @Given elementsFactory: (@Given AppComponent) -> Set<ComponentElement<AppComponent>>,
    @Given initializersFactory: (@Given AppComponent) -> Set<ComponentInitializer<AppComponent>>
) {
    initializeApp(elementsFactory, initializersFactory)
}

@ComponentInitializerBinding
@Given
fun essentialsAppComponentInitializer(
    @Given appInitializerRunner: AppInitializerRunner,
    @Given appWorkerRunner: AppWorkerRunner
): ComponentInitializer<AppComponent> = {
    appInitializerRunner()
    appWorkerRunner()
}
