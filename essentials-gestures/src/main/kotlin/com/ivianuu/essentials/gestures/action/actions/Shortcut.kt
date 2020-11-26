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
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.ImagePainter
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionFactory
import com.ivianuu.essentials.gestures.action.ActionFactoryBinding
import com.ivianuu.essentials.gestures.action.ActionPickerDelegate
import com.ivianuu.essentials.gestures.action.ActionPickerDelegateBinding
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.shortcutpicker.Shortcut
import com.ivianuu.essentials.shortcutpicker.ShortcutPickerPage
import com.ivianuu.essentials.ui.core.Icon
import com.ivianuu.essentials.ui.image.toBitmap
import com.ivianuu.essentials.ui.image.toImageBitmap
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.stringResource
import java.io.ByteArrayOutputStream

@ActionFactoryBinding
class ShortcutActionFactory(
    private val
    logger: Logger,
    private val sendIntent: sendIntent,
) : ActionFactory {
    override fun handles(key: String): Boolean = key.startsWith(ACTION_KEY_PREFIX)
    override suspend fun createAction(key: String): Action {
        logger.d("create action from $key")
        val tmp = key.split(DELIMITER)
        val label = tmp[1]
        val intent = Intent.getIntent(tmp[2])
        val iconBytes = Base64.decode(tmp[3], 0)
        val icon = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.size).toImageBitmap()
        return Action(
            key = key,
            title = label,
            unlockScreen = true,
            enabled = true,
            icon = singleActionIcon { Icon(ImagePainter(icon)) }
        )
    }

    override suspend fun createExecutor(key: String): ActionExecutor {
        val tmp = key.split(DELIMITER)
        val intent = Intent.getIntent(tmp[2])
        return { sendIntent(intent) }
    }
}

@ActionPickerDelegateBinding
class ShortcutActionPickerDelegate(
    private val navigator: Navigator,
    private val shortcutPickerPage: ShortcutPickerPage,
    private val stringResource: stringResource,
) : ActionPickerDelegate {
    override val title: String
        get() = stringResource(R.string.es_action_shortcut)
    override val icon: @Composable () -> Unit = {
        Icon(R.drawable.es_ic_content_cut)
    }

    override suspend fun getResult(): ActionPickerResult? {
        val shortcut = navigator.push<Shortcut> { shortcutPickerPage(null) }
            ?: return null

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
        return ActionPickerResult.Action(key)
    }
}

private const val ACTION_KEY_PREFIX = "shortcut"
private const val DELIMITER = "=:="
