package com.ivianuu.essentials.app

import com.ivianuu.injekt.Given
import com.ivianuu.injekt.component.App
import com.ivianuu.injekt.component.AppComponent
import com.ivianuu.injekt.component.ComponentElement
import com.ivianuu.injekt.component.ComponentElementBinding
import com.ivianuu.injekt.component.appComponent
import com.ivianuu.injekt.component.get
import com.ivianuu.injekt.component.initializeApp

fun App.initializeEssentials(
    @Given elementsFactory: (@Given AppComponent) -> Set<ComponentElement<AppComponent>>
) {
    initializeApp(elementsFactory)
    with(appComponent.get<EsInitComponent>()) {
        appInitializerRunner()
        appWorkerRunner()
    }
}

@ComponentElementBinding<AppComponent>
@Given
class EsInitComponent(
    @Given val appInitializerRunner: AppInitializerRunner,
    @Given val appWorkerRunner: AppWorkerRunner
)
