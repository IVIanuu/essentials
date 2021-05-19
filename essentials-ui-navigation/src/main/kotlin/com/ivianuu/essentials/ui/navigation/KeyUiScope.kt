package com.ivianuu.essentials.ui.navigation

import com.ivianuu.essentials.ui.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.scope.*

typealias KeyUiScope = Scope

@Provide val keyUiScopeModule = ChildScopeModule1<UiScope, Key<*>, KeyUiScope>()
