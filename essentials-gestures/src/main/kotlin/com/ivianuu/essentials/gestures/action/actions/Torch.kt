package com.ivianuu.essentials.gestures.action.actions

import androidx.compose.Composable
import androidx.ui.foundation.Icon
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.FlashOff
import androidx.ui.material.icons.filled.FlashOn
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionIconProvider
import com.ivianuu.essentials.gestures.action.bindAction
import com.ivianuu.essentials.torch.TorchManager
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Factory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal fun ComponentBuilder.esTorchActionBindings() {
    // todo check if device has a torch packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    bindAction(
        key = "torch",
        title = { getStringResource(R.string.es_action_torch) },
        iconProvider = { get<TorchActionIconProvider>() },
        executor = { get<TorchActionExecutor>() }
    )
}

@Factory
internal class TorchActionExecutor(
    private val torchManager: TorchManager
) : ActionExecutor {
    override suspend fun invoke() {
        torchManager.toggleTorch()
    }
}

@Factory
internal class TorchActionIconProvider(
    private val torchManager: TorchManager
) : ActionIconProvider {
    override val icon: Flow<@Composable () -> Unit>
        get() = torchManager.torchState
            .map {
                if (it) Icons.Default.FlashOn
                else Icons.Default.FlashOff
            }
            .map {
                {
                    Icon(it)
                }
            }
}
