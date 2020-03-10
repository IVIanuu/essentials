package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Repeat
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.RecentAppsProvider
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.action
import com.ivianuu.essentials.gestures.action.actionPermission
import com.ivianuu.essentials.util.SystemBuildInfo
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Lazy
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.parametersOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

internal val EsLastAppActionModule = Module {
    action(
        key = "last_app",
        title = { getStringResource(R.string.es_action_last_app) },
        iconProvider = { SingleActionIconProvider(Icons.Default.Repeat) },
        permissions = { listOf(actionPermission { accessibility }) },
        unlockScreen = { true },
        executor = { get<LastAppActionExecutor>() }
    )
}

@Factory
internal class LastAppActionExecutor(
    private val context: Context,
    private val lazyRecentAppsExecutor: Lazy<AccessibilityActionExecutor>,
    private val intentExecutorProvider: Provider<IntentActionExecutor>,
    private val packageManager: PackageManager,
    private val recentAppsProvider: RecentAppsProvider,
    private val systemBuildInfo: SystemBuildInfo
) : ActionExecutor {
    override suspend fun invoke() {
        if (systemBuildInfo.sdk >= 24) {
            val executor =
                lazyRecentAppsExecutor(parameters = parametersOf(AccessibilityService.GLOBAL_ACTION_RECENTS))
            executor()
            delay(250)
            executor()
        } else {
            val recentApps = recentAppsProvider.recentsApps.first()
                .filter { it != getHomePackage() }
            d { "recent apps $recentApps" }
            val lastApp = recentApps.getOrNull(1) ?: return
            val intent = packageManager.getLaunchIntentForPackage(lastApp) ?: return
            intentExecutorProvider(parameters = parametersOf(intent))()
        }
    }

    private fun getHomePackage(): String {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }

        return context.packageManager.resolveActivity(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )?.activityInfo?.packageName ?: ""
    }
}
