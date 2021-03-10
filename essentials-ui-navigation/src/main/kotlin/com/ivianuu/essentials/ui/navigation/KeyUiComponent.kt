package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import com.ivianuu.essentials.ui.UiComponent
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.component.ChildComponentModule0
import com.ivianuu.injekt.component.Component

typealias KeyUiComponent = Component

@Given
val keyUiComponentModule = ChildComponentModule0<UiComponent, KeyUiComponent>()

val LocalKeyUiComponent = staticCompositionLocalOf<KeyUiComponent> {
    error("No key ui component provided")
}
