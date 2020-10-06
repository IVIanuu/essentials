package com.ivianuu.essentials.gestures.action.actions

import android.view.KeyEvent
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionBinding

@ActionBinding
fun mediaSkipPreviousAction(mediaAction: mediaAction) = mediaAction(
    "media_skip_previous",
    KeyEvent.KEYCODE_MEDIA_PREVIOUS,
    R.string.es_action_media_skip_previous,
    singleActionIcon(R.drawable.es_ic_skip_previous)
)
