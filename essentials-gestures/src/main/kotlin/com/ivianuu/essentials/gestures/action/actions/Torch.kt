package com.ivianuu.essentials.gestures.action.actions

import androidx.compose.Composable
import androidx.ui.foundation.Icon
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.FlashOff
import androidx.ui.material.icons.filled.FlashOn
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionIconProvider
import com.ivianuu.essentials.gestures.action.BindAction
import com.ivianuu.essentials.torch.TorchManager
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@BindAction
@Reader
fun torchAction() = Action(
    key = "torch",
    title = Resources.getString(R.string.es_action_torch),
    iconProvider = given<TorchActionIconProvider>(),
    executor = given<TorchActionExecutor>()
)

@Given
@Reader
internal class TorchActionExecutor : ActionExecutor {
    override suspend fun invoke() {
        given<TorchManager>().toggleTorch()
    }
}

@Given
@Reader
internal class TorchActionIconProvider : ActionIconProvider {
    override val icon: Flow<@Composable () -> Unit>
        get() = given<TorchManager>().torchState
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
