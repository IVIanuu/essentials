package com.ivianuu.essentials.gestures.action.actions

import android.view.KeyEvent
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding

@ActionBinding
fun mediaSkipNextAction(mediaAction: mediaAction): Action = mediaAction(
    "media_skip_next",
    KeyEvent.KEYCODE_MEDIA_NEXT,
    R.string.es_action_media_skip_next,
    singleActionIcon(R.drawable.es_ic_skip_next)
)
