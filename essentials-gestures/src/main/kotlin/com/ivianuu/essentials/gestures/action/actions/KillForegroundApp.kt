package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.content.pm.PackageManager
import com.ivianuu.essentials.gestures.RecentAppsProvider
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.Transient
import com.ivianuu.injekt.composition.installIn
import kotlinx.coroutines.flow.first

@Module
private fun KillForegroundAppModule() {
    installIn<ApplicationComponent>()
    /*bindAction<@ActionQualifier("kill_foreground_action") Action>(
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
    )*/
}

@Transient
internal class KillForegroundAppActionExecutor(
    private val buildInfo: BuildInfo,
    private val recentAppsProvider: RecentAppsProvider,
    private val packageManager: PackageManager,
    private val rootActionExecutorProvider: @Provider (String) -> RootActionExecutor
) : ActionExecutor {
    override suspend fun invoke() {
        val currentApp = recentAppsProvider.currentApp.first()

        if (currentApp != "android" &&
            currentApp != "com.android.systemui" &&
            currentApp != buildInfo.packageName && // we have no suicidal intentions :D
            currentApp != getHomePackage()
        ) {
            rootActionExecutorProvider("am force-stop $currentApp")()
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
