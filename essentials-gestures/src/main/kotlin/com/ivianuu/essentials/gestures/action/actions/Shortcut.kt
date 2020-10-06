package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.ImagePainter
import androidx.compose.ui.res.vectorResource
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionFactory
import com.ivianuu.essentials.gestures.action.ActionFactoryBinding
import com.ivianuu.essentials.gestures.action.ActionPickerDelegate
import com.ivianuu.essentials.gestures.action.ActionPickerDelegateBinding
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.shortcutpicker.Shortcut
import com.ivianuu.essentials.shortcutpicker.ShortcutPickerPage
import com.ivianuu.essentials.ui.image.toBitmap
import com.ivianuu.essentials.ui.image.toImageAsset
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.util.Logger
import com.ivianuu.essentials.util.Resources
import java.io.ByteArrayOutputStream

@ActionFactoryBinding
class ShortcutActionFactory(
    private val logger: Logger,
    private val sendIntent: sendIntent,
) : ActionFactory {
    override fun handles(key: String): Boolean = key.startsWith(ACTION_KEY_PREFIX)
    override suspend fun createAction(key: String): Action {
        logger.d("create action from $key")
        val tmp = key.split(DELIMITER)
        val label = tmp[1]
        val intent = Intent.getIntent(tmp[2])
        val iconBytes = Base64.decode(tmp[3], 0)
        val icon = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.size).toImageAsset()
        return Action(
            key = key,
            title = label,
            unlockScreen = true,
            enabled = true,
            icon = singleActionIcon { Icon(ImagePainter(icon)) },
            execute = { sendIntent(intent) }
        )
    }
}

@ActionPickerDelegateBinding
class ShortcutActionPickerDelegate(
    private val navigator: Navigator,
    private val resources: Resources,
    private val shortcutPickerPage: ShortcutPickerPage,
) : ActionPickerDelegate {
    override val title: String
        get() = resources.getString(R.string.es_action_shortcut)
    override val icon: @Composable () -> Unit = {
        Icon(vectorResource(R.drawable.es_ic_content_cut))
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
                )}"
        return ActionPickerResult.Action(key)
    }
}

private const val ACTION_KEY_PREFIX = "shortcut"
private const val DELIMITER = "=:="
