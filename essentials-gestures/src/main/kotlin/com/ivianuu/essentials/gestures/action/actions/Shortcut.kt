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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionFactory
import com.ivianuu.essentials.gestures.action.ActionPickerDelegate
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerKey
import com.ivianuu.essentials.shortcutpicker.ShortcutPickerKey
import com.ivianuu.essentials.store.Sink
import com.ivianuu.essentials.ui.image.toBitmap
import com.ivianuu.essentials.ui.image.toImageBitmap
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.pushAndAwait
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import java.io.ByteArrayOutputStream

@Given
class ShortcutActionFactory(
    @Given private val actionIntentSender: ActionIntentSender,
    @Given private val logger: Logger
) : ActionFactory {
    override suspend fun handles(id: String): Boolean = id.startsWith(ACTION_KEY_PREFIX)
    override suspend fun createAction(id: String): Action {
        logger.d { "create action from $id" }
        val tmp = id.split(DELIMITER)
        val label = tmp[1]

        val iconBytes = Base64.decode(tmp[3], 0)
        val icon = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.size).toImageBitmap()
        return Action(
            id = id,
            title = label,
            unlockScreen = true,
            enabled = true,
            icon = singleActionIcon { Image(icon, null) }
        )
    }

    @Suppress("DEPRECATION")
    override suspend fun createExecutor(id: String): ActionExecutor {
        val tmp = id.split(DELIMITER)
        val intent = Intent.getIntent(tmp[2])
        return { actionIntentSender(intent) }
    }
}

@Given
class ShortcutActionPickerDelegate(
    @Given private val navigator: Sink<NavigationAction>,
    @Given private val resourceProvider: ResourceProvider,
) : ActionPickerDelegate {
    override val title: String
        get() = resourceProvider.string(R.string.es_action_shortcut)
    override val icon: @Composable () -> Unit = {
        Icon(painterResource(R.drawable.es_ic_content_cut), null)
    }

    override suspend fun getResult(): ActionPickerKey.Result? {
        val shortcut = navigator.pushAndAwait(ShortcutPickerKey()) ?: return null
        val label = shortcut.name
        val icon = shortcut.icon.toBitmap()
        val stream = ByteArrayOutputStream()
        icon.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val iconBytes = stream.toByteArray()
        val key =
            "$ACTION_KEY_PREFIX$DELIMITER$label$DELIMITER${shortcut.intent.toUri(0)}$DELIMITER${
                Base64.encodeToString(
                    iconBytes,
                    0
                )
            }"
        return ActionPickerKey.Result.Action(key)
    }
}

private const val ACTION_KEY_PREFIX = "shortcut"
private const val DELIMITER = "=:="
