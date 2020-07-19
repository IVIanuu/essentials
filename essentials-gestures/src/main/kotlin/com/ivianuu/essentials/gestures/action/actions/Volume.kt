package com.ivianuu.essentials.gestures.action.actions

import android.media.AudioManager
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.VolumeUp
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.BindAction
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given

@BindAction
@Reader
fun volumeAction() = Action(
    key = "volume",
    title = Resources.getString(R.string.es_action_volume),
    iconProvider = SingleActionIconProvider(Icons.Default.VolumeUp),
    executor = given<VolumeActionExecutor>()
)

@Given
internal class VolumeActionExecutor : ActionExecutor {
    override suspend fun invoke() {
        given<AudioManager>().adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_SAME,
            AudioManager.FLAG_SHOW_UI
        )
    }
}
