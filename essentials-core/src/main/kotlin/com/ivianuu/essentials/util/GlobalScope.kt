package com.ivianuu.essentials.util

import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Distinct
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.CoroutineScope

@Distinct
typealias GlobalScope = CoroutineScope

@Reader
inline val globalScope: GlobalScope
    get() = given()

object GlobalScopeModule {

    @Given(ApplicationComponent::class)
    fun globalScope(): GlobalScope = CoroutineScope(dispatchers.default)

}
