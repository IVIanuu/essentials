package com.ivianuu.essentials.gestures.action.actions

import android.view.KeyEvent
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.PlayArrow
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.injekt.ApplicationScope
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Module

@ApplicationScope
@Module
private fun ComponentBuilder.mediaPlayPauseAction() {
    bindMediaAction(
        key = "media_play_pause",
        keycode = KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
        titleRes = R.string.es_action_media_play_pause,
        icon = { Icon(Icons.Default.PlayArrow) }
    )
}
