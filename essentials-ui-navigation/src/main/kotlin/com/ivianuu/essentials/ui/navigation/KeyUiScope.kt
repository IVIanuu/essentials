package com.ivianuu.essentials.ui.navigation

import com.ivianuu.essentials.util.ScopeCoroutineScope
import com.ivianuu.injekt.Given
import kotlinx.coroutines.CoroutineScope

typealias KeyUiScope = CoroutineScope

@Given
inline fun @Given ScopeCoroutineScope<KeyUiComponent>.keyUiScope(): KeyUiScope = this
