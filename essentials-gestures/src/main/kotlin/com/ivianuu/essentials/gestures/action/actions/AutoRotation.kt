package com.ivianuu.essentials.gestures.action.actions

/**
import com.ivianuu.pie.R
import com.ivianuu.pie.action.Action
import com.ivianuu.pie.data.Flag
import kotlinx.coroutines.flow.Flow

private fun createAutoRotationFlow(): Flow<String> {
    return settings.autoRotation.asFlow()
        .onOffStateFromInt()
}

private fun createAutoRotationAction() = Action(
    key = KEY_AUTO_ROTATION,
    title = string(R.string.action_auto_rotation),
    states = onOff(
        R.drawable.ic_screen_rotation,
        R.drawable.ic_screen_lock_rotation
    ),
    flags = setOf(Flag.RequiresWriteSettingsPermission)
)

private suspend fun toggleAutoRotation() {
    settings.autoRotation.set(
        if (settings.autoRotation.get() != 1) {
            1
        } else {
            0
        }
    )
}*/