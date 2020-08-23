package com.ivianuu.essentials.gestures.action.actions

import android.view.KeyEvent
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.GivenAction

@GivenAction
fun mediaSkipPreviousAction() = mediaAction(
    key = "media_skip_previous",
    keycode = KeyEvent.KEYCODE_MEDIA_PREVIOUS,
    titleRes = R.string.es_action_media_skip_previous,
    icon = singleActionIcon(R.drawable.es_ic_skip_previous)
)
