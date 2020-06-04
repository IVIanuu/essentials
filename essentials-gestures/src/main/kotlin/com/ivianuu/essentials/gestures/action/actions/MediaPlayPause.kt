package com.ivianuu.essentials.gestures.action.actions

import android.view.KeyEvent
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.PlayArrow
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionQualifier
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.composition.installIn

@Module
private fun MediaPlayPauseModule() {
    installIn<ApplicationComponent>()
    bindMediaAction<@ActionQualifier("media_play_pause") Action>(
        key = "media_play_pause",
        keycode = KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
        titleRes = R.string.es_action_media_play_pause,
        icon = { Icon(Icons.Default.PlayArrow) }
    )
}
