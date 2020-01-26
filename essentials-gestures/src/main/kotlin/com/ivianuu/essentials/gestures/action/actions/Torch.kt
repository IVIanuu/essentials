package com.ivianuu.essentials.gestures.action.actions

import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionIconProvider
import com.ivianuu.essentials.gestures.action.bindAction
import com.ivianuu.essentials.icon.Essentials
import com.ivianuu.essentials.icon.EssentialsIcons
import com.ivianuu.essentials.material.icons.Icons
import com.ivianuu.essentials.material.icons.filled.FlashOff
import com.ivianuu.essentials.material.icons.filled.FlashOn
import com.ivianuu.essentials.material.icons.group
import com.ivianuu.essentials.material.icons.lazyMaterialIcon
import com.ivianuu.essentials.material.icons.path
import com.ivianuu.essentials.torch.TorchManager
import com.ivianuu.essentials.ui.painter.Renderable
import com.ivianuu.essentials.ui.painter.VectorRenderable
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal val EsTorchActionModule = Module {
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
    override val icon: Flow<Renderable>
        get() = torchManager.torchState
            .map {
                if (it) Icons.Default.FlashOn
                else Icons.Default.FlashOff
            }
            .map { VectorRenderable(it) }
}
