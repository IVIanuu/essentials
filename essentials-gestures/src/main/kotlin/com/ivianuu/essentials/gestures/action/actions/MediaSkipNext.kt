package com.ivianuu.essentials.gestures.action.actions

import android.view.KeyEvent
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.GivenAction

@GivenAction
fun mediaSkipNextAction() = mediaAction(
    key = "media_skip_next",
    keycode = KeyEvent.KEYCODE_MEDIA_NEXT,
    titleRes = R.string.es_action_media_skip_next,
    icon = singleActionIcon(R.drawable.es_ic_skip_next)
)
