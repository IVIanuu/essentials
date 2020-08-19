package com.ivianuu.essentials.gestures.action.actions

import android.view.KeyEvent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.GivenAction

@GivenAction
fun mediaPlayPauseAction() = mediaAction(
    key = "media_play_pause",
    keycode = KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
    titleRes = R.string.es_action_media_play_pause,
    icon = singleActionIcon(Icons.Default.PlayArrow)
)
