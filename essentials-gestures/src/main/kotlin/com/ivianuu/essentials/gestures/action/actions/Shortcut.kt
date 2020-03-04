package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.Composable
import androidx.ui.graphics.vector.VectorAsset
import androidx.ui.graphics.vector.path
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.lazyMaterialIcon
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionFactory
import com.ivianuu.essentials.gestures.action.ActionPickerDelegate
import com.ivianuu.essentials.gestures.action.bindActionFactory
import com.ivianuu.essentials.gestures.action.bindActionPickerDelegate
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.icon.Essentials
import com.ivianuu.essentials.icon.EssentialsIcons
import com.ivianuu.essentials.shortcutpicker.Shortcut
import com.ivianuu.essentials.shortcutpicker.ShortcutPickerRoute
import com.ivianuu.essentials.ui.image.Image
import com.ivianuu.essentials.ui.image.VectorImage
import com.ivianuu.essentials.ui.image.toBitmap
import com.ivianuu.essentials.ui.image.toImageAsset
import com.ivianuu.essentials.ui.navigation.NavigatorState
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Param
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.parametersOf
import java.io.ByteArrayOutputStream

internal val EsShortcutActionModule = Module {
    bindActionFactory<ShortcutActionFactory>()
    bindActionPickerDelegate<ShortcutActionPickerDelegate>()
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
    private val intentActionExecutorProvider: Provider<IntentActionExecutor>
) : ActionFactory {
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
            iconProvider = SingleActionIconProvider { Image(icon) },
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
    override val icon: @Composable () -> Unit
        get() = { VectorImage(Icons.Essentials.ContentCut) }

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

val EssentialsIcons.ContentCut: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(6.0f, 16.0f)
        curveTo(7.1045694f, 16.0f, 8.0f, 16.89543f, 8.0f, 18.0f)
        curveTo(8.0f, 19.10457f, 7.1045694f, 20.0f, 6.0f, 20.0f)
        curveTo(4.8954306f, 20.0f, 4.0f, 19.10457f, 4.0f, 18.0f)
        curveTo(4.0f, 16.89543f, 4.8954306f, 16.0f, 6.0f, 16.0f)
        close()
    }
    path {
        moveTo(12.0f, 11.5f)
        curveTo(12.276142f, 11.5f, 12.5f, 11.723858f, 12.5f, 12.0f)
        curveTo(12.5f, 12.276142f, 12.276142f, 12.5f, 12.0f, 12.5f)
        curveTo(11.723858f, 12.5f, 11.5f, 12.276142f, 11.5f, 12.0f)
        curveTo(11.5f, 11.723858f, 11.723858f, 11.5f, 12.0f, 11.5f)
        close()
    }
    path {
        moveTo(6.0f, 4.0f)
        curveTo(7.1045694f, 4.0f, 8.0f, 4.8954306f, 8.0f, 6.0f)
        curveTo(8.0f, 7.1045694f, 7.1045694f, 8.0f, 6.0f, 8.0f)
        curveTo(4.8954306f, 8.0f, 4.0f, 7.1045694f, 4.0f, 6.0f)
        curveTo(4.0f, 4.8954306f, 4.8954306f, 4.0f, 6.0f, 4.0f)
        close()
    }
    path {
        moveTo(9.64f, 7.64f)
        curveToRelative(0.23f, -0.5f, 0.36f, -1.05f, 0.36f, -1.64f)
        curveToRelative(0.0f, -2.21f, -1.79f, -4.0f, -4.0f, -4.0f)
        reflectiveCurveTo(2.0f, 3.79f, 2.0f, 6.0f)
        reflectiveCurveToRelative(1.79f, 4.0f, 4.0f, 4.0f)
        curveToRelative(0.59f, 0.0f, 1.14f, -0.13f, 1.64f, -0.36f)
        lineTo(10.0f, 12.0f)
        lineToRelative(-2.36f, 2.36f)
        curveTo(7.14f, 14.13f, 6.59f, 14.0f, 6.0f, 14.0f)
        curveToRelative(-2.21f, 0.0f, -4.0f, 1.79f, -4.0f, 4.0f)
        reflectiveCurveToRelative(1.79f, 4.0f, 4.0f, 4.0f)
        reflectiveCurveToRelative(4.0f, -1.79f, 4.0f, -4.0f)
        curveToRelative(0.0f, -0.59f, -0.13f, -1.14f, -0.36f, -1.64f)
        lineTo(12.0f, 14.0f)
        lineToRelative(7.0f, 7.0f)
        horizontalLineToRelative(3.0f)
        verticalLineToRelative(-1.0f)
        lineTo(9.64f, 7.64f)
        close()
        moveTo(6.0f, 8.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, -0.89f, -2.0f, -2.0f)
        reflectiveCurveToRelative(0.9f, -2.0f, 2.0f, -2.0f)
        reflectiveCurveToRelative(2.0f, 0.89f, 2.0f, 2.0f)
        reflectiveCurveToRelative(-0.9f, 2.0f, -2.0f, 2.0f)
        close()
        moveToRelative(0.0f, 12.0f)
        curveToRelative(-1.1f, 0.0f, -2.0f, -0.89f, -2.0f, -2.0f)
        reflectiveCurveToRelative(0.9f, -2.0f, 2.0f, -2.0f)
        reflectiveCurveToRelative(2.0f, 0.89f, 2.0f, 2.0f)
        reflectiveCurveToRelative(-0.9f, 2.0f, -2.0f, 2.0f)
        close()
        moveToRelative(6.0f, -7.5f)
        curveToRelative(-0.28f, 0.0f, -0.5f, -0.22f, -0.5f, -0.5f)
        reflectiveCurveToRelative(0.22f, -0.5f, 0.5f, -0.5f)
        reflectiveCurveToRelative(0.5f, 0.22f, 0.5f, 0.5f)
        reflectiveCurveToRelative(-0.22f, 0.5f, -0.5f, 0.5f)
        close()
        moveTo(19.0f, 3.0f)
        lineToRelative(-6.0f, 6.0f)
        lineToRelative(2.0f, 2.0f)
        lineToRelative(7.0f, -7.0f)
        verticalLineTo(3.0f)
        close()
    }
}

private const val ACTION_KEY_PREFIX = "shortcut"
private const val DELIMITER = "=:="
