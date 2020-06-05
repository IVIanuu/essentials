package com.ivianuu.essentials.gestures.action.actions

import android.view.KeyEvent
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.SkipNext
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.StringKey
import com.ivianuu.injekt.composition.installIn

@Module
private fun MediaSkipNextModule() {
    installIn<ApplicationComponent>()
    bindMediaAction<@StringKey("media_skip_next") Action>(
        key = "media_skip_next",
        keycode = KeyEvent.KEYCODE_MEDIA_NEXT,
        titleRes = R.string.es_action_media_skip_next,
        icon = { Icon(Icons.Default.SkipNext) }
    )
}