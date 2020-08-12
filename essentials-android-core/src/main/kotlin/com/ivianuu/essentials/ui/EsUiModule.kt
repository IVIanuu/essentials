package com.ivianuu.essentials.ui

import com.ivianuu.essentials.util.dispatchers
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.android.ActivityStorage
import com.ivianuu.injekt.given
import kotlinx.coroutines.CoroutineScope

typealias UiScope = CoroutineScope

@Reader
inline val uiScope: UiScope
    get() = given()

object EsUiModule {

    @Given(ActivityStorage::class)
    fun uiScope(): UiScope = CoroutineScope(dispatchers.main)

}
