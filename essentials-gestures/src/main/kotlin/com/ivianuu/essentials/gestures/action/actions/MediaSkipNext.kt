package com.ivianuu.essentials.gestures.action.actions

import android.view.KeyEvent
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.SkipNext
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.injekt.ApplicationScope
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Module

@ApplicationScope
@Module
private fun ComponentBuilder.mediaSkipNextAction() {
    bindMediaAction(
        key = "media_skip_next",
        keycode = KeyEvent.KEYCODE_MEDIA_NEXT,
        titleRes = R.string.es_action_media_skip_next,
        icon = { Icon(Icons.Default.SkipNext) }
    )
}