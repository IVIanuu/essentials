package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.content.pm.PackageManager
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Clear
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.RecentAppsProvider
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.action
import com.ivianuu.essentials.gestures.action.actionPermission
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.injekt.ApplicationScope
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.parametersOf
import kotlinx.coroutines.flow.first

@ApplicationScope
@Module
private fun ComponentBuilder.killForegroundAppAction() {
    action(
        key = "kill_foreground_action",
        title = { getStringResource(R.string.es_action_kill_foreground_app) },
        iconProvider = { SingleActionIconProvider(Icons.Default.Clear) },
        permissions = {
            listOf(
                actionPermission { accessibility },
                actionPermission { root }
            )
        },
        executor = { get<KillForegroundAppActionExecutor>() }
    )
}

@Factory
private class KillForegroundAppActionExecutor(
    private val buildInfo: BuildInfo,
    private val recentAppsProvider: RecentAppsProvider,
    private val packageManager: PackageManager,
    private val rootActionExecutorProvider: Provider<RootActionExecutor>
) : ActionExecutor {
    override suspend fun invoke() {
        val currentApp = recentAppsProvider.currentApp.first()

        if (currentApp != "android" &&
            currentApp != "com.android.systemui" &&
            currentApp != buildInfo.packageName && // we have no suicidal intentions :D
            currentApp != getHomePackage()
        ) {
            rootActionExecutorProvider(parameters = parametersOf("am force-stop $currentApp"))()
        }
    }

    private fun getHomePackage(): String {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }

        return packageManager.resolveActivity(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )?.activityInfo?.packageName ?: ""
    }

}
