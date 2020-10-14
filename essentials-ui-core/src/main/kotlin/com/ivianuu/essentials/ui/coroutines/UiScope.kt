package com.ivianuu.essentials.ui.coroutines

import com.ivianuu.essentials.util.MainDispatcher
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.android.ActivityComponent
import kotlinx.coroutines.CoroutineScope

typealias UiScope = CoroutineScope

@Binding(ActivityComponent::class)
fun uiScope(mainDispatcher: MainDispatcher): UiScope =
    CoroutineScope(mainDispatcher)
