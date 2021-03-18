package com.ivianuu.essentials.state

import com.ivianuu.essentials.app.ScopeWorker
import com.ivianuu.essentials.store.Collector
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.component.Component
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

typealias SourceFlow<T, S> = Flow<T>

@Given
fun <@Given F : SourceFlow<T, S>, T, S : Component> sourceFlowWorker(
    @Given flow: F,
    @Given collector: Collector<T>
): ScopeWorker<S> = {
    flow.collect { value ->
        collector(value)
    }
}

typealias EffectFlow<S> = Flow<*>

@Given
fun <@Given F : EffectFlow<S>, S : Component> effectFlowWorker(@Given flow: F): ScopeWorker<S> = {
    flow.collect()
}