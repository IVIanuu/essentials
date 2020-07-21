package com.ivianuu.essentials.shortcutpicker

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import com.ivianuu.essentials.coroutines.parallelMap
import com.ivianuu.essentials.ui.image.toImageAsset
import com.ivianuu.essentials.util.dispatchers
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.withContext

@Reader
suspend fun getShortcuts(): List<Shortcut> = withContext(dispatchers.io) {
    val pm = given<PackageManager>()
    val shortcutsIntent = Intent(Intent.ACTION_CREATE_SHORTCUT)
    pm.queryIntentActivities(shortcutsIntent, 0)
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
                    name = resolveInfo.loadLabel(pm).toString(),
                    icon = resolveInfo.loadIcon(pm).toImageAsset()
                )
            } catch (e: Exception) {
                null
            }
        }
        .filterNotNull()
        .sortedBy { it.name }
}

@Reader
internal suspend fun extractShortcut(shortcutRequestResult: Intent): Shortcut =
    withContext(dispatchers.default) {
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
                    given<PackageManager>().getResourcesForApplication(iconResource.packageName)
                val id =
                    resources.getIdentifier(iconResource.resourceName, null, null)
                resources.getDrawable(id).toImageAsset()
            }
            else -> error("no icon provided $shortcutRequestResult")
        }

        Shortcut(intent, name, icon)
    }
