package com.ivianuu.essentials.ui.navigation

import com.ivianuu.essentials.util.ComponentCoroutineScope
import com.ivianuu.injekt.Given
import kotlinx.coroutines.CoroutineScope

typealias KeyUiScope = CoroutineScope

@Given
inline fun @Given ComponentCoroutineScope<KeyUiComponent>.keyUiScope(): KeyUiScope = this
