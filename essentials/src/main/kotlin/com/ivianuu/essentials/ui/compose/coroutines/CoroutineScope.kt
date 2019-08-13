package com.ivianuu.essentials.ui.compose.coroutines

import com.ivianuu.compose.ComponentComposition
import com.ivianuu.compose.memo
import com.ivianuu.compose.onDispose
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

val ComponentComposition.coroutineScope: CoroutineScope
    get() {
        return memo {
            val scope = CoroutineScope(Job() + Dispatchers.Main)
            onDispose { scope.coroutineContext[Job]?.cancel() }
            scope
        }
    }