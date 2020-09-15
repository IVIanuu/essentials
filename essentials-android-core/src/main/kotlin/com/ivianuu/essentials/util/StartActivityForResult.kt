package com.ivianuu.essentials.util

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.onActive
import com.ivianuu.essentials.ui.common.registerActivityResultCallback
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.navigation.navigator
import com.ivianuu.injekt.Reader
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@Reader
suspend fun startActivityForResult(intent: Intent): ActivityResult =
    startActivityForResult(ActivityResultContracts.StartActivityForResult(), intent)

@Reader
suspend fun <I, O> startActivityForResult(
    contract: ActivityResultContract<I, O>,
    input: I
): O {
    startUi()
    return suspendCancellableCoroutine { continuation ->
        var popped = false
        fun popIfNeeded() {
            if (!popped) {
                popped = true
                navigator.popTop()
            }
        }

        navigator.push(
            Route(opaque = true) {
                val launcher = registerActivityResultCallback(
                    contract,
                    ActivityResultCallback {
                        popIfNeeded()
                        continuation.resume(it)
                    }
                )

                onActive { launcher.launch(input) }
            }
        )
        continuation.invokeOnCancellation { popIfNeeded() }
    }
}
