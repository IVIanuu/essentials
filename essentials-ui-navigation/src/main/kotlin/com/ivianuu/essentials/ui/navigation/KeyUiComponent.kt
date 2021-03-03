package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import com.ivianuu.essentials.ui.UiComponent
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.component.Component
import com.ivianuu.injekt.component.ComponentElementBinding

typealias KeyUiComponent = Component

@ComponentElementBinding<UiComponent>
@Given
fun keyUiComponentFactory(
    @Given parent: UiComponent,
    @Given builderFactory: () -> Component.Builder<KeyUiComponent>,
): () -> KeyUiComponent = {
    builderFactory()
        .dependency(parent)
        .build()
}

val LocalKeyUiComponent = staticCompositionLocalOf<KeyUiComponent> {
    error("No key ui component provided")
}
