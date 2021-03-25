package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import com.ivianuu.essentials.ui.UiGivenScope
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.ChildGivenScopeModule0
import com.ivianuu.injekt.scope.DefaultGivenScope

typealias KeyUiGivenScope = DefaultGivenScope

@Given
val keyUiGivenScopeModule = ChildGivenScopeModule0<UiGivenScope, KeyUiGivenScope>()

val LocalKeyUiGivenScope = staticCompositionLocalOf<KeyUiGivenScope> {
    error("No KeyUiGivenScope provided")
}
