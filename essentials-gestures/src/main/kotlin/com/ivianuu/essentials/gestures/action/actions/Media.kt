package com.ivianuu.essentials.gestures.action.actions

import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import androidx.compose.Composable
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionPrefs
import com.ivianuu.essentials.gestures.action.action
import com.ivianuu.essentials.store.getCurrentData
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Param
import com.ivianuu.injekt.parametersOf

internal fun ComponentBuilder.bindMediaAction(
    key: String,
    keycode: Int,
    titleRes: Int,
    icon: @Composable () -> Unit
) {
    action(
        key = key,
        title = { getStringResource(titleRes) },
        iconProvider = { SingleActionIconProvider(icon) },
        executor = {
            get<MediaActionExecutor>(parameters = parametersOf(keycode))
        }
    )
}

@Factory
private class MediaActionExecutor(
    @Param private val keycode: Int,
    private val actionPrefs: ActionPrefs,
    private val context: Context
) : ActionExecutor {
    override suspend fun invoke() {
        suspend fun mediaIntent(keyEvent: Int) = Intent(Intent.ACTION_MEDIA_BUTTON).apply {
            putExtra(
                Intent.EXTRA_KEY_EVENT,
                KeyEvent(keyEvent, keycode)
            )

            val mediaApp = actionPrefs.actionMediaApp.getCurrentData()
            if (mediaApp != null) {
                `package` = mediaApp
            }
        }

        context.sendOrderedBroadcast(mediaIntent(KeyEvent.ACTION_DOWN), null)
        context.sendOrderedBroadcast(mediaIntent(KeyEvent.ACTION_UP), null)
    }
}
