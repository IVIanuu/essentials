package com.ivianuu.essentials.gestures.action.actions

import androidx.compose.Composable
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.FlashOff
import androidx.ui.material.icons.filled.FlashOn
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionIconProvider
import com.ivianuu.essentials.gestures.action.action
import com.ivianuu.essentials.torch.TorchManager
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.injekt.ApplicationScope
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Module
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@ApplicationScope
@Module
private fun ComponentBuilder.torchAction() {
    // todo check if device has a torch packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    action(
        key = "torch",
        title = { getStringResource(R.string.es_action_torch) },
        iconProvider = { get<TorchActionIconProvider>() },
        executor = { get<TorchActionExecutor>() }
    )
}

@Factory
private class TorchActionExecutor(
    private val torchManager: TorchManager
) : ActionExecutor {
    override suspend fun invoke() {
        torchManager.toggleTorch()
    }
}

@Factory
private class TorchActionIconProvider(
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
