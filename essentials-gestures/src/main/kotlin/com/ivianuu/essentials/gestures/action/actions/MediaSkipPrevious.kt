package com.ivianuu.essentials.gestures.action.actions

import android.view.KeyEvent
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.SkipPrevious
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.injekt.ComponentBuilder

internal fun ComponentBuilder.mediaSkipPreviousAction() {
    bindMediaAction(
        key = "media_skip_previous",
        keycode = KeyEvent.KEYCODE_MEDIA_PREVIOUS,
        titleRes = R.string.es_action_media_skip_previous,
        icon = { Icon(Icons.Default.SkipPrevious) }
    )
}
