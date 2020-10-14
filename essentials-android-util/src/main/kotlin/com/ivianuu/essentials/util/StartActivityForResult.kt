package com.ivianuu.essentials.util

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.onActive
import com.ivianuu.essentials.ui.common.registerActivityResultCallback
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@FunBinding
suspend fun startActivityForIntentResult(
    startActivityForResult: startActivityForResult<Intent, ActivityResult>,
    intent: @Assisted Intent,
): ActivityResult {
    return startActivityForResult(ActivityResultContracts.StartActivityForResult(), intent)
}

@FunBinding
suspend fun <I, O> startActivityForResult(
    startUi: startUi,
    navigator: Navigator,
    contract: @Assisted ActivityResultContract<I, O>,
    input: @Assisted I,
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
