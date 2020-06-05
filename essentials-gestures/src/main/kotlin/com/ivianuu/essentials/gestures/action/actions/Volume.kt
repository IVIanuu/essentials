package com.ivianuu.essentials.gestures.action.actions

import android.media.AudioManager
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Transient
import com.ivianuu.injekt.composition.installIn

@Module
private fun volumeModule() {
    installIn<ApplicationComponent>()
    /*bindAction<@ActionQualifier("volume") Action>(
        key = "volume",
        title = { getStringResource(R.string.es_action_volume) },
        iconProvider = { SingleActionIconProvider(Icons.Default.VolumeUp) },
        executor = { get<VolumeActionExecutor>() }
    )*/
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
