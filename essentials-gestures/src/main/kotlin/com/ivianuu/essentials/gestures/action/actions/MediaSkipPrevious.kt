package com.ivianuu.essentials.gestures.action.actions

import android.view.KeyEvent
import androidx.ui.foundation.Icon
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.SkipPrevious
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.StringKey
import com.ivianuu.injekt.composition.installIn

@Module
fun MediaSkipPreviousModule() {
    installIn<ApplicationComponent>()
    bindMediaAction<@StringKey("media_skip_previous") Action>(
        key = "media_skip_previous",
        keycode = KeyEvent.KEYCODE_MEDIA_PREVIOUS,
        titleRes = R.string.es_action_media_skip_previous,
        icon = { Icon(Icons.Default.SkipPrevious) }
    )
}
