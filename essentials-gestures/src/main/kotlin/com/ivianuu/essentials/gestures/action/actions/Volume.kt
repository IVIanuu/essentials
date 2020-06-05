package com.ivianuu.essentials.gestures.action.actions

import android.media.AudioManager
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.VolumeUp
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.action
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.StringKey
import com.ivianuu.injekt.Transient
import com.ivianuu.injekt.composition.installIn

@Module
private fun volumeModule() {
    installIn<ApplicationComponent>()
    action { resourceProvider: ResourceProvider,
             executor: VolumeActionExecutor ->
        Action(
            key = "volume",
            title = resourceProvider.getString(R.string.es_action_volume),
            iconProvider = SingleActionIconProvider(Icons.Default.VolumeUp),
            executor = executor
        ) as @StringKey("volume") Action
    }
}

@Transient
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
