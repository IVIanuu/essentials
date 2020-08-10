package com.ivianuu.essentials.gestures.action.actions

import android.view.KeyEvent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipPrevious
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.BindAction
import com.ivianuu.injekt.Reader

@BindAction
@Reader
fun mediaSkipPreviousAction() = mediaAction(
    key = "media_skip_previous",
    keycode = KeyEvent.KEYCODE_MEDIA_PREVIOUS,
    titleRes = R.string.es_action_media_skip_previous,
    icon = singleActionIcon(Icons.Default.SkipPrevious)
)
