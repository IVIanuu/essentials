package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.content.pm.PackageManager
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Clear
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.RecentAppsProvider
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.GivenAction
import com.ivianuu.essentials.gestures.action.permissions
import com.ivianuu.essentials.gestures.action.plus
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.flow.first

@GivenAction
fun killForegroundAction() = Action(
    key = "kill_foreground_action",
    title = Resources.getString(R.string.es_action_kill_foreground_app),
    icon = singleActionIcon(Icons.Default.Clear),
    permissions = permissions { accessibility + root },
    execute = { killApp() }
)

@Reader
private suspend fun killApp() {
    val currentApp = given<RecentAppsProvider>().currentApp.first()

    if (currentApp != "android" &&
        currentApp != "com.android.systemui" &&
        currentApp != given<BuildInfo>().packageName && // we have no suicidal intentions :D
        currentApp != getHomePackage()
    ) {
        runRootCommand("am force-stop $currentApp")
    }
}

@Reader
private fun getHomePackage(): String {
    val intent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_HOME)
    }

    return given<PackageManager>().resolveActivity(
        intent,
        PackageManager.MATCH_DEFAULT_ONLY
    )?.activityInfo?.packageName ?: ""
}
