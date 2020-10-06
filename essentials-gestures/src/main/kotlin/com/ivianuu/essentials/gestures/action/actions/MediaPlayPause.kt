package com.ivianuu.essentials.gestures.action.actions

import android.view.KeyEvent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionBinding

@ActionBinding
fun mediaPlayPauseAction(mediaAction: mediaAction) = mediaAction(
    "media_play_pause",
    KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
    R.string.es_action_media_play_pause,
    singleActionIcon(Icons.Default.PlayArrow)
)
