package com.ivianuu.essentials.gestures.action.actions

import androidx.compose.Composable
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.FlashOff
import androidx.ui.material.icons.filled.FlashOn
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionIconProvider
import com.ivianuu.essentials.torch.TorchManager
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Transient
import com.ivianuu.injekt.composition.installIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Module
private fun TorchModule() {
    installIn<ApplicationComponent>()
    // todo check if device has a torch packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    /*bindAction<@ActionQualifier("torch") Action>(
        key = "torch",
        title = { getStringResource(R.string.es_action_torch) },
        iconProvider = { get<TorchActionIconProvider>() },
        executor = { get<TorchActionExecutor>() }
    )*/
}

@Transient
internal class TorchActionExecutor(
    private val torchManager: TorchManager
) : ActionExecutor {
    override suspend fun invoke() {
        torchManager.toggleTorch()
    }
}

@Transient
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
