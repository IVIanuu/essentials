/*
 * Copyright 2020 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.view.KeyEvent
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.ActionMediaAppPref
import com.ivianuu.essentials.util.stringResource
import com.ivianuu.injekt.FunApi
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.android.ApplicationContext
import kotlinx.coroutines.flow.first

@FunBinding
fun mediaAction(
    doMediaAction: doMediaAction,
    stringResource: stringResource,
    @FunApi key: String,
    @FunApi titleRes: Int,
    @FunApi icon: ActionIcon
) = Action(
    key = key,
    title = stringResource(titleRes),
    icon = icon
)

@FunBinding
suspend fun doMediaAction(
    applicationContext: ApplicationContext,
    mediaIntent: mediaIntent,
    @FunApi keycode: Int
) {
    applicationContext.sendOrderedBroadcast(mediaIntent(KeyEvent.ACTION_DOWN, keycode), null)
    applicationContext.sendOrderedBroadcast(mediaIntent(KeyEvent.ACTION_UP, keycode), null)
}

@FunBinding
suspend fun mediaIntent(
    mediaAppPref: ActionMediaAppPref,
    @FunApi keyEvent: Int,
    @FunApi keycode: Int
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
