package com.ivianuu.essentials.gestures.action.actions

import android.media.AudioManager
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.util.stringResource

@ActionBinding
fun volumeAction(
    audioManager: AudioManager,
    stringResource: stringResource,
): Action = Action(
    key = "volume",
    title = stringResource(R.string.es_action_volume),
    icon = singleActionIcon(R.drawable.es_ic_volume_up),
    execute = {
        audioManager.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_SAME,
            AudioManager.FLAG_SHOW_UI
        )
    }
)
