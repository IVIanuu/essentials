package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.view.KeyEvent
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.ActionMediaAppPref
import com.ivianuu.essentials.util.stringResource
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.android.ApplicationContext
import kotlinx.coroutines.flow.first

@FunBinding
fun mediaAction(
    doMediaAction: doMediaAction,
    stringResource: stringResource,
    key: @Assisted String,
    keycode: @Assisted Int,
    titleRes: @Assisted Int,
    icon: @Assisted ActionIcon,
): Action = Action(
    key = key,
    title = stringResource(titleRes),
    icon = icon,
    execute = { doMediaAction(keycode) }
)

@FunBinding
suspend fun doMediaAction(
    applicationContext: ApplicationContext,
    mediaIntent: mediaIntent,
    keycode: @Assisted Int,
) {
    applicationContext.sendOrderedBroadcast(mediaIntent(KeyEvent.ACTION_DOWN, keycode), null)
    applicationContext.sendOrderedBroadcast(mediaIntent(KeyEvent.ACTION_UP, keycode), null)
}

@FunBinding
suspend fun mediaIntent(
    mediaAppPref: ActionMediaAppPref,
    keyEvent: @Assisted Int,
    keycode: @Assisted Int,
): Intent = Intent(Intent.ACTION_MEDIA_BUTTON).apply {
    putExtra(
        Intent.EXTRA_KEY_EVENT,
        KeyEvent(keyEvent, keycode)
    )

    val mediaApp = mediaAppPref.data.first()
    if (mediaApp != null) {
        `package` = mediaApp
    }
}

