package com.ivianuu.essentials.gestures.action.actions

/**
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import kotlinx.coroutines.flow.Flow

private fun createTorchAction() = Action(
    key = KEY_TORCH,
    title = string(R.string.action_torch),
    states = onOff(R.drawable.ic_torch_on, R.drawable.es_ic_torch_off)
)

private fun createTorchFlow(): Flow<String> {
    return torchManager.torchState.onOffStateFromBoolean()
}

private suspend fun toggleTorch() {
    torchManager.toggleTorch()
}
*/