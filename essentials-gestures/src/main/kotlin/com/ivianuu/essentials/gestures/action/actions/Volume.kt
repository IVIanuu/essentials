package com.ivianuu.essentials.gestures.action.actions

import android.media.AudioManager
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.VolumeUp
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.action
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Factory

internal fun ComponentBuilder.volumeAction() {
    action(
        key = "volume",
        title = { getStringResource(R.string.es_action_volume) },
        iconProvider = { SingleActionIconProvider(Icons.Default.VolumeUp) },
        executor = { get<VolumeActionExecutor>() }
    )
}

@Factory
private class VolumeActionExecutor(
    private val audioManager: AudioManager
) : ActionExecutor {
    override suspend fun invoke() {
        audioManager.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_SAME,
            AudioManager.FLAG_SHOW_UI
        )
    }
}
