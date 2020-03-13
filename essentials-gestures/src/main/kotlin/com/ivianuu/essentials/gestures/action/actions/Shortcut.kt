package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.Composable
import androidx.ui.foundation.Icon
import androidx.ui.graphics.painter.ImagePainter
import androidx.ui.res.vectorResource
import com.ivianuu.essentials.android.ui.image.toBitmap
import com.ivianuu.essentials.android.ui.image.toImageAsset
import com.ivianuu.essentials.android.ui.navigation.NavigatorState
import com.ivianuu.essentials.android.util.ResourceProvider
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionFactory
import com.ivianuu.essentials.gestures.action.ActionPickerDelegate
import com.ivianuu.essentials.gestures.action.bindActionFactoryIntoSet
import com.ivianuu.essentials.gestures.action.bindActionPickerDelegateIntoSet
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.shortcutpicker.Shortcut
import com.ivianuu.essentials.shortcutpicker.ShortcutPickerRoute
import com.ivianuu.essentials.util.Logger
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Param
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.parametersOf
import java.io.ByteArrayOutputStream

internal fun ComponentBuilder.shortcutAction() {
    bindActionFactoryIntoSet<ShortcutActionFactory>()
    bindActionPickerDelegateIntoSet<ShortcutActionPickerDelegate>()
}

@Factory
internal class ShortcutActionExecutor(
    @Param private val intent: Intent,
    private val lazyDelegate: Provider<IntentActionExecutor>
) : ActionExecutor {
    override suspend fun invoke() {
        lazyDelegate(parameters = parametersOf(intent))()
    }
}

@Factory
internal class ShortcutActionFactory(
    private val intentActionExecutorProvider: Provider<IntentActionExecutor>,
    private val logger: Logger
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
            iconProvider = SingleActionIconProvider { Icon(ImagePainter(icon)) },
            executor = intentActionExecutorProvider(parameters = parametersOf(intent))
        )
    }
}

@Factory
internal class ShortcutActionPickerDelegate(
    private val resourceProvider: ResourceProvider
) : ActionPickerDelegate {
    override val title: String
        get() = resourceProvider.getString(R.string.es_action_shortcut)
    override val icon: @Composable () -> Unit = {
        Icon(vectorResource(R.drawable.es_ic_content_cut))
    }

    override suspend fun getResult(navigator: NavigatorState): ActionPickerResult? {
        val shortcut = navigator.push<Shortcut>(
            ShortcutPickerRoute()
        ) ?: return null

        val label = shortcut.name
        val icon = shortcut.icon.toBitmap()
        val stream = ByteArrayOutputStream()
        icon.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val iconBytes = stream.toByteArray()
        val key = "$ACTION_KEY_PREFIX$DELIMITER$label$DELIMITER${shortcut.intent.toUri(0)}$DELIMITER${Base64.encodeToString(iconBytes, 0)}"
        return ActionPickerResult.Action(key)
    }
}

private const val ACTION_KEY_PREFIX = "shortcut"
private const val DELIMITER = "=:="
