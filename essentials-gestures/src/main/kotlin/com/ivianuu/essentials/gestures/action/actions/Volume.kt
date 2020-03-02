package com.ivianuu.essentials.gestures.action.actions

import android.media.AudioManager
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.bindAction
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.VolumeUp
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.get

internal val EsVolumeActionModule = Module {
    bindAction(
        key = "volume",
        title = { getStringResource(R.string.es_action_volume) },
        iconProvider = { SingleActionIconProvider(Icons.Default.VolumeUp) },
        executor = { get<VolumeActionExecutor>() }
    )
}

@Factory
internal class VolumeActionExecutor(
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
