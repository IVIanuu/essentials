package com.ivianuu.essentials.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.android.ApplicationLifecycleOwner
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@FunBinding
fun appForegroundState(
    lifecycleOwner: ApplicationLifecycleOwner,
): Flow<Boolean> = callbackFlow {
    var wasInForeground = false
    val observer = LifecycleEventObserver { source, _ ->
        val isInForeground = source.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)
        if (wasInForeground != isInForeground) {
            wasInForeground = isInForeground
            offer(isInForeground)
        }
    }
    offer(wasInForeground)
    lifecycleOwner.lifecycle.addObserver(observer)
    awaitClose { lifecycleOwner.lifecycle.removeObserver(observer) }
}
