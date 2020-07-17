package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.content.pm.PackageManager
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Clear
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.RecentAppsProvider
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.BindAction
import com.ivianuu.essentials.gestures.action.permissions
import com.ivianuu.essentials.gestures.action.plus
import com.ivianuu.essentials.util.BuildInfo
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.flow.first

@BindAction
@Reader
fun killForegroundAction() = Action(
    key = "kill_foreground_action",
    title = Resources.getString(R.string.es_action_kill_foreground_app),
    iconProvider = SingleActionIconProvider(Icons.Default.Clear),
    permissions = permissions { accessibility + root },
    executor = given<KillForegroundAppActionExecutor>()
)

@Given
@Reader
internal class KillForegroundAppActionExecutor : ActionExecutor {
    override suspend fun invoke() {
        val currentApp = given<RecentAppsProvider>().currentApp.first()

        if (currentApp != "android" &&
            currentApp != "com.android.systemui" &&
            currentApp != given<BuildInfo>().packageName && // we have no suicidal intentions :D
            currentApp != getHomePackage()
        ) {
            given<(String) -> RootActionExecutor>()("am force-stop $currentApp")()
        }
    }

    private fun getHomePackage(): String {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
        }

        return given<PackageManager>().resolveActivity(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        )?.activityInfo?.packageName ?: ""
    }

}
