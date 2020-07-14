package com.ivianuu.essentials.gestures.action.actions

import android.view.KeyEvent
import androidx.ui.foundation.Icon
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.SkipPrevious
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.bindAction
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.SetElements

@SetElements(ApplicationComponent::class)
@Reader
fun mediaSkipPreviousAction() = bindMediaAction(
    key = "media_skip_previous",
    keycode = KeyEvent.KEYCODE_MEDIA_PREVIOUS,
    titleRes = R.string.es_action_media_skip_previous,
    icon = { Icon(Icons.Default.SkipPrevious) }
)
