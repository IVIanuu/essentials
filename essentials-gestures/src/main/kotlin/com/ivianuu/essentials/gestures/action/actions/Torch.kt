package com.ivianuu.essentials.gestures.action.actions

import androidx.compose.foundation.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.BindAction
import com.ivianuu.essentials.torch.TorchManager
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.flow.map

@BindAction
@Reader
fun torchAction() = Action(
    key = "torch",
    title = Resources.getString(R.string.es_action_torch),
    icon = torchIcon(),
    execute = { given<TorchManager>().toggleTorch() }
)

@Reader
private fun torchIcon(): ActionIcon = given<TorchManager>().torchState
    .map {
        if (it) Icons.Default.FlashOn
        else Icons.Default.FlashOff
    }
    .map {
        {
            Icon(it)
        }
    }
