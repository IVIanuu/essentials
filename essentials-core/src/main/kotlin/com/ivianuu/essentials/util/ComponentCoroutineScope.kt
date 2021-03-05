package com.ivianuu.essentials.util

import com.ivianuu.essentials.coroutines.DefaultDispatcher
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.common.ForTypeKey
import com.ivianuu.injekt.common.ScopeDisposable
import com.ivianuu.injekt.common.invoke
import com.ivianuu.injekt.component.Component
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

typealias ComponentCoroutineScope<C> = CoroutineScope

@Given
fun <@ForTypeKey C : Component> componentCoroutineScope(
    @Given component: C,
    @Given defaultDispatcher: DefaultDispatcher
): ComponentCoroutineScope<C> = component {
    object : CoroutineScope, ScopeDisposable {
        override val coroutineContext: CoroutineContext = Job() + defaultDispatcher
        override fun dispose() {
            coroutineContext.cancel()
        }
    }
}
