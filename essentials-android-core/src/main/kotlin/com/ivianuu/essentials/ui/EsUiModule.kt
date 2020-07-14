package com.ivianuu.essentials.ui

import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.essentials.util.dispatchers
import com.ivianuu.injekt.Distinct
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.android.ActivityComponent
import com.ivianuu.injekt.given
import kotlinx.coroutines.CoroutineScope

@Distinct
typealias UiScope = CoroutineScope

@Reader
inline val uiScope: UiScope
    get() = given()

object EsUiModule {

    @Given(ActivityComponent::class)
    @Reader
    fun uiScope(): UiScope = CoroutineScope(dispatchers.main)

}
