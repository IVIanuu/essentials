package com.ivianuu.essentials.gestures.action.actions

import android.view.KeyEvent
import androidx.ui.foundation.Icon
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.SkipNext
import androidx.ui.material.icons.filled.SkipPrevious
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.BindAction
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.SetElements

@BindAction
@Reader
fun mediaSkipNextAction() = mediaAction(
    key = "media_skip_next",
    keycode = KeyEvent.KEYCODE_MEDIA_NEXT,
    titleRes = R.string.es_action_media_skip_next,
    icon = { Icon(Icons.Default.SkipNext) }
)
