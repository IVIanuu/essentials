package com.ivianuu.essentials.ui.navigation

import androidx.compose.runtime.*
import com.ivianuu.essentials.ui.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.scope.*

typealias KeyUiGivenScope = GivenScope

@Given
val keyUiGivenScopeModule = ChildGivenScopeModule1<UiGivenScope, Key<*>, KeyUiGivenScope>()
