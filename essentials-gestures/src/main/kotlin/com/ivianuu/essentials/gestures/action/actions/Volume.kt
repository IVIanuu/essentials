package com.ivianuu.essentials.gestures.action.actions

import android.media.AudioManager
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.GivenAction
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.given

@GivenAction
fun volumeAction() = Action(
    key = "volume",
    title = Resources.getString(R.string.es_action_volume),
    icon = singleActionIcon(R.drawable.es_ic_volume_up),
    execute = {
        given<AudioManager>().adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_SAME,
            AudioManager.FLAG_SHOW_UI
        )
    }
)
