package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.view.KeyEvent
import com.ivianuu.essentials.app.androidApplicationContext
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.ActionPrefs
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.flow.first

@Reader
fun mediaAction(
    key: String,
    keycode: Int,
    titleRes: Int,
    icon: ActionIcon
) = Action(
    key = key,
    title = Resources.getString(titleRes),
    icon = icon,
    execute = { doMediaAction(keycode) }
)

@Reader
private suspend fun doMediaAction(keycode: Int) {
    androidApplicationContext.sendOrderedBroadcast(mediaIntent(KeyEvent.ACTION_DOWN, keycode), null)
    androidApplicationContext.sendOrderedBroadcast(mediaIntent(KeyEvent.ACTION_UP, keycode), null)
}

@Reader
private suspend fun mediaIntent(
    keyEvent: Int,
    keycode: Int
) = Intent(Intent.ACTION_MEDIA_BUTTON).apply {
    putExtra(
        Intent.EXTRA_KEY_EVENT,
        KeyEvent(keyEvent, keycode)
    )

    val mediaApp = given<ActionPrefs>().actionMediaApp.data.first()
    if (mediaApp != null) {
        `package` = mediaApp
    }
}

