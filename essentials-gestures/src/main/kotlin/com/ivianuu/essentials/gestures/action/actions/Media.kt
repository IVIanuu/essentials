package com.ivianuu.essentials.gestures.action.actions

import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import androidx.compose.Composable
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionPrefs
import com.ivianuu.essentials.gestures.action.action
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.ForApplication
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.Transient
import kotlinx.coroutines.flow.first

@Module
internal fun <T : Action> bindMediaAction(
    key: String,
    keycode: Int,
    titleRes: Int,
    icon: @Composable () -> Unit
) {
    action { resourceProvider: ResourceProvider,
             executorFactory: @Provider (Int) -> MediaActionExecutor ->
        Action(
            key = key,
            title = resourceProvider.getString(titleRes),
            iconProvider = SingleActionIconProvider(icon),
            executor = executorFactory(keycode)
        ) as T
    }
}

@Transient
internal class MediaActionExecutor(
    private val keycode: @Assisted Int,
    private val actionPrefs: ActionPrefs,
    private val context: @ForApplication Context
) : ActionExecutor {
    override suspend fun invoke() {
        suspend fun mediaIntent(keyEvent: Int) = Intent(Intent.ACTION_MEDIA_BUTTON).apply {
            putExtra(
                Intent.EXTRA_KEY_EVENT,
                KeyEvent(keyEvent, keycode)
            )

            val mediaApp = actionPrefs.actionMediaApp.data.first()
            if (mediaApp != null) {
                `package` = mediaApp
            }
        }

        context.sendOrderedBroadcast(mediaIntent(KeyEvent.ACTION_DOWN), null)
        context.sendOrderedBroadcast(mediaIntent(KeyEvent.ACTION_UP), null)
    }
}
