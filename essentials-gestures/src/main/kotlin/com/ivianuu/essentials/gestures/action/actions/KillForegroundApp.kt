package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.RecentAppsProvider
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.gestures.action.choosePermissions
import com.ivianuu.essentials.gestures.action.plus
import com.ivianuu.essentials.gestures.currentApp
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.FunBinding
import kotlinx.coroutines.flow.first

@ActionBinding
fun killForegroundAction(
    choosePermissions: choosePermissions,
    killApp: killApp,
    resources: Resources,
): Action = Action(
    key = "kill_foreground_action",
    title = resources.getString(R.string.es_action_kill_foreground_app),
    icon = singleActionIcon(Icons.Default.Clear),
    permissions = choosePermissions { accessibility + root },
    execute = { killApp() }
)

@FunBinding
suspend fun killApp(
    buildInfo: BuildInfo,
    getHomePackage: getHomePackage,
    recentAppsProvider: RecentAppsProvider,
    runRootCommand: runRootCommand,
) {
    val currentApp = recentAppsProvider.currentApp.first()

    if (currentApp != "android" &&
        currentApp != "com.android.systemui" &&
        currentApp != buildInfo.packageName && // we have no suicidal intentions :D
        currentApp != getHomePackage()
    ) {
        runRootCommand("am force-stop $currentApp")
    }
}

@FunBinding
fun getHomePackage(
    packageManager: PackageManager,
): String {
    val intent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_HOME)
    }

    return packageManager.resolveActivity(
        intent,
        PackageManager.MATCH_DEFAULT_ONLY
    )?.activityInfo?.packageName ?: ""
}
