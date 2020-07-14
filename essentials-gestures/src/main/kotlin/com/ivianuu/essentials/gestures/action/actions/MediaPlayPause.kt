package com.ivianuu.essentials.gestures.action.actions

import android.view.KeyEvent
import androidx.ui.foundation.Icon
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.PlayArrow
import androidx.ui.material.icons.filled.SkipPrevious
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.SetElements

@SetElements(ApplicationComponent::class)
@Reader
fun mediaPlayPauseAction() = bindMediaAction(
    key = "media_play_pause",
    keycode = KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
    titleRes = R.string.es_action_media_play_pause,
    icon = { Icon(Icons.Default.PlayArrow) }
)
