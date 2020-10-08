package com.ivianuu.essentials.gestures.action.actions

import androidx.compose.foundation.Icon
import androidx.compose.ui.res.vectorResource
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.torch.TorchManager
import com.ivianuu.essentials.util.stringResource
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.flow.map

@ActionBinding
fun torchAction(
    stringResource: stringResource,
    torchIcon: torchIcon,
    torchManager: TorchManager,
): Action = Action(
    key = "torch",
    title = stringResource(R.string.es_action_torch),
    icon = torchIcon(),
    execute = { torchManager.toggleTorch() }
)

@FunBinding
fun torchIcon(
    torchManager: TorchManager,
): ActionIcon = torchManager.torchState
    .map {
        if (it) R.drawable.es_ic_flash_on
        else R.drawable.es_ic_flash_off
    }
    .map {
        {
            Icon(vectorResource(it))
        }
    }
