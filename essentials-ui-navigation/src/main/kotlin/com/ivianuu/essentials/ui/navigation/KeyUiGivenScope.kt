package com.ivianuu.essentials.ui.navigation

import com.ivianuu.essentials.ui.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.scope.*

typealias KeyUiGivenScope = GivenScope

@Given val keyUiGivenScopeModule = ChildScopeModule1<UiGivenScope, Key<*>, KeyUiGivenScope>()
