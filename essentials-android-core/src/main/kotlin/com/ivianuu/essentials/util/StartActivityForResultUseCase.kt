package com.ivianuu.essentials.util

import android.content.Intent
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.onActive
import com.ivianuu.essentials.ui.common.registerActivityResultCallback
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.navigation.RouteAmbient
import com.ivianuu.injekt.Transient
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@Transient
class StartActivityForResultUseCase(
    private val navigator: Navigator,
    private val startUiUseCase: StartUiUseCase
) {

    suspend operator fun invoke(intent: Intent): ActivityResult =
        invoke(ActivityResultContracts.StartActivityForResult(), intent)

    suspend operator fun <I, O> invoke(
        contract: ActivityResultContract<I, O>,
        input: I
    ): O {
        return suspendCancellableCoroutine { continuation ->
            startUiUseCase()
            navigator.push(
                Route(opaque = true) {
                    val route = RouteAmbient.current
                    val launcher = registerActivityResultCallback(
                        contract
                    ) {
                        navigator.pop(route = route)
                        continuation.resume(it)
                    }

                    onActive { launcher.launch(input) }
                }
            )
        }
    }

}
