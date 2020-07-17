package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.Composable
import androidx.ui.foundation.Icon
import androidx.ui.graphics.painter.ImagePainter
import androidx.ui.res.vectorResource
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionFactory
import com.ivianuu.essentials.gestures.action.ActionPickerDelegate
import com.ivianuu.essentials.gestures.action.BindActionFactory
import com.ivianuu.essentials.gestures.action.BindActionPickerDelegate
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.shortcutpicker.Shortcut
import com.ivianuu.essentials.shortcutpicker.ShortcutPickerPage
import com.ivianuu.essentials.ui.image.toBitmap
import com.ivianuu.essentials.ui.image.toImageAsset
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.navigator
import com.ivianuu.essentials.util.Resources
import com.ivianuu.essentials.util.d
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import java.io.ByteArrayOutputStream

@BindActionFactory
@Given
@Reader
internal class ShortcutActionFactory : ActionFactory {
    override fun handles(key: String): Boolean = key.startsWith(ACTION_KEY_PREFIX)
    override suspend fun createAction(key: String): Action {
        d { "create action from $key" }
        val tmp = key.split(DELIMITER)
        val label = tmp[1]
        val intent = Intent.getIntent(tmp[2])
        val iconBytes = Base64.decode(tmp[3], 0)
        val icon = BitmapFactory.decodeByteArray(iconBytes, 0, iconBytes.size).toImageAsset()
        return Action(
            key = key,
            title = label,
            unlockScreen = true,
            iconProvider = SingleActionIconProvider { Icon(ImagePainter(icon)) },
            executor = given<(Intent) -> IntentActionExecutor>()(intent),
            enabled = true
        )
    }
}


@BindActionPickerDelegate
@Given
@Reader
internal class ShortcutActionPickerDelegate : ActionPickerDelegate {
    override val title: String
        get() = Resources.getString(R.string.es_action_shortcut)
    override val icon: @Composable () -> Unit = {
        Icon(vectorResource(R.drawable.es_ic_content_cut))
    }

    override suspend fun getResult(): ActionPickerResult? {
        val shortcut = navigator.push<Shortcut> { ShortcutPickerPage() }
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
