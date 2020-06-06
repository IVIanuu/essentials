package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.content.pm.PackageManager
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Clear
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.RecentAppsProvider
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionPermissions
import com.ivianuu.essentials.gestures.action.action
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.StringKey
import com.ivianuu.injekt.Transient
import com.ivianuu.injekt.composition.installIn
import kotlinx.coroutines.flow.first

@Module
private fun KillForegroundAppModule() {
    installIn<ApplicationComponent>()
    action { resourceProvider: ResourceProvider,
             permissions: ActionPermissions,
             executor: KillForegroundAppActionExecutor ->
        Action(
            key = "kill_foreground_action",
            title = resourceProvider.getString(R.string.es_action_kill_foreground_app),
            iconProvider = SingleActionIconProvider(Icons.Default.Clear),
            permissions = listOf(
                permissions.accessibility,
                permissions.root
            ),
            executor = executor
        ) as @StringKey("kill_foreground_action") Action
    }
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
