package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.scope.*

typealias KeyUiGivenScope = DefaultGivenScope

@Given
val keyUiGivenScopeModule = ChildGivenScopeModule0<UiGivenScope, KeyUiGivenScope>()

val LocalKeyUiGivenScope = staticCompositionLocalOf<KeyUiGivenScope> {
    error("No KeyUiGivenScope provided")
}
