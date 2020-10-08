package com.ivianuu.essentials.ui

import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.android.ActivityComponent
import kotlinx.coroutines.CoroutineScope

typealias UiScope = CoroutineScope

@Binding(ActivityComponent::class)
fun uiScope(dispatchers: AppCoroutineDispatchers): UiScope =
    CoroutineScope(dispatchers.main)
