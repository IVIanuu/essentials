package com.ivianuu.essentials.shortcutpicker

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import com.ivianuu.essentials.coroutines.parallelMap
import com.ivianuu.essentials.ui.image.toImageAsset
import com.ivianuu.essentials.util.DefaultDispatcher
import com.ivianuu.essentials.util.IODispatcher
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.withContext

@FunBinding
suspend fun getShortcuts(
    ioDispatcher: IODispatcher,
    packageManager: PackageManager,
): List<Shortcut> = withContext(ioDispatcher) {
    val shortcutsIntent = Intent(Intent.ACTION_CREATE_SHORTCUT)
    packageManager.queryIntentActivities(shortcutsIntent, 0)
        .parallelMap { resolveInfo ->
            try {
                Shortcut(
                    intent = Intent().apply {
                        action = Intent.ACTION_CREATE_SHORTCUT
                        component = ComponentName(
                            resolveInfo.activityInfo.packageName,
                            resolveInfo.activityInfo.name
                        )
                    },
                    name = resolveInfo.loadLabel(packageManager).toString(),
                    icon = resolveInfo.loadIcon(packageManager).toImageAsset()
                )
            } catch (t: Throwable) {
                null
            }
        }
        .filterNotNull()
        .sortedBy { it.name }
}

@FunBinding
suspend fun extractShortcut(
    defaultDispatcher: DefaultDispatcher,
    packageManager: PackageManager,
    shortcutRequestResult: @Assisted Intent,
): Shortcut = withContext(defaultDispatcher) {
    val intent =
        shortcutRequestResult.getParcelableExtra<Intent>(Intent.EXTRA_SHORTCUT_INTENT)!!
    val name = shortcutRequestResult.getStringExtra(Intent.EXTRA_SHORTCUT_NAME)!!
    val bitmapIcon =
        shortcutRequestResult.getParcelableExtra<Bitmap>(Intent.EXTRA_SHORTCUT_ICON)
    val iconResource =
        shortcutRequestResult.getParcelableExtra<Intent.ShortcutIconResource>(Intent.EXTRA_SHORTCUT_ICON_RESOURCE)

    val icon = when {
        bitmapIcon != null -> bitmapIcon.toImageAsset()
        iconResource != null -> {
            val resources =
                packageManager.getResourcesForApplication(iconResource.packageName)
            val id =
                resources.getIdentifier(iconResource.resourceName, null, null)
            resources.getDrawable(id).toImageAsset()
        }
        else -> error("no icon provided $shortcutRequestResult")
    }

    Shortcut(intent, name, icon)
}
