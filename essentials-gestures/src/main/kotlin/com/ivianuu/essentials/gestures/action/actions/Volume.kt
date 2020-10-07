package com.ivianuu.essentials.gestures.action.actions

import android.media.AudioManager
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.util.Resources

@ActionBinding
fun volumeAction(
    audioManager: AudioManager,
    resources: Resources,
): Action = Action(
    key = "volume",
    title = resources.getString(R.string.es_action_volume),
    icon = singleActionIcon(R.drawable.es_ic_volume_up),
    execute = {
        audioManager.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_SAME,
            AudioManager.FLAG_SHOW_UI
        )
    }
)
