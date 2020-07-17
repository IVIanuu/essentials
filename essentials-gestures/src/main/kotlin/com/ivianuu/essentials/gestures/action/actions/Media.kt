package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.view.KeyEvent
import androidx.compose.Composable
import com.ivianuu.essentials.app.applicationContext
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionPrefs
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.flow.first

@Reader
fun mediaAction(
    key: String,
    keycode: Int,
    titleRes: Int,
    icon: @Composable () -> Unit
) = Action(
    key = key,
    title = Resources.getString(titleRes),
    iconProvider = SingleActionIconProvider(icon),
    executor = given<(Int) -> MediaActionExecutor>()(keycode)
)

@Given
@Reader
internal class MediaActionExecutor(
    private val keycode: Int
) : ActionExecutor {
    override suspend fun invoke() {
        suspend fun mediaIntent(keyEvent: Int) = Intent(Intent.ACTION_MEDIA_BUTTON).apply {
            putExtra(
                Intent.EXTRA_KEY_EVENT,
                KeyEvent(keyEvent, keycode)
            )

            val mediaApp = given<ActionPrefs>().actionMediaApp.data.first()
            if (mediaApp != null) {
                `package` = mediaApp
            }
        }

        applicationContext.sendOrderedBroadcast(mediaIntent(KeyEvent.ACTION_DOWN), null)
        applicationContext.sendOrderedBroadcast(mediaIntent(KeyEvent.ACTION_UP), null)
    }
}
