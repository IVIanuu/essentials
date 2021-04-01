package com.ivianuu.essentials.store

import com.ivianuu.injekt.Given
import kotlinx.coroutines.flow.MutableSharedFlow

typealias Collector<T> = (T) -> Unit

@Given
fun <T> collector(@Given flow: MutableSharedFlow<T>): Collector<T> = {
    flow.tryEmit(it)
}
