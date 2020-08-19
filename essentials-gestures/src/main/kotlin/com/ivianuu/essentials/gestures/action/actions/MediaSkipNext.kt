package com.ivianuu.essentials.gestures.action.actions

import android.view.KeyEvent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipNext
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.GivenAction

@GivenAction
fun mediaSkipNextAction() = mediaAction(
    key = "media_skip_next",
    keycode = KeyEvent.KEYCODE_MEDIA_NEXT,
    titleRes = R.string.es_action_media_skip_next,
    icon = singleActionIcon(Icons.Default.SkipNext)
)
