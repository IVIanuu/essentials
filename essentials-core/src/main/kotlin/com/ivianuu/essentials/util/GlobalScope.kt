package com.ivianuu.essentials.util

import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.merge.ApplicationComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

typealias GlobalScope = CoroutineScope

@Binding(ApplicationComponent::class)
fun globalScope(defaultDispatcher: DefaultDispatcher): GlobalScope =
    CoroutineScope(defaultDispatcher)

@FunBinding
suspend fun globalTask(
    globalScope: GlobalScope,
    block: @Assisted suspend CoroutineScope.() -> Unit,
) {
    globalScope.launch(block = block).join()
}
