package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.Composable
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Apps
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.AppStore
import com.ivianuu.essentials.apps.coil.AppIcon
import com.ivianuu.essentials.apps.ui.AppPickerRoute
import com.ivianuu.essentials.apps.ui.LaunchableAppFilter
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionFactory
import com.ivianuu.essentials.gestures.action.ActionIconProvider
import com.ivianuu.essentials.gestures.action.ActionPickerDelegate
import com.ivianuu.essentials.gestures.action.actionFactory
import com.ivianuu.essentials.gestures.action.actionPickerDelegate
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Assisted
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.Transient
import com.ivianuu.injekt.composition.installIn
import kotlinx.coroutines.flow.Flow

@Module
private fun AppActionModule() {
    installIn<ApplicationComponent>()
    actionFactory<AppActionFactory>()
    actionPickerDelegate<AppActionPickerDelegate>()
}

@Transient
internal class AppActionExecutor(
    private val packageName: @Assisted String,
    private val packageManager: PackageManager,
    private val delegateProvider: @Provider (Intent) -> IntentActionExecutor,
    private val toaster: Toaster
) : ActionExecutor {
    override suspend fun invoke() {
        try {
            delegateProvider(
                packageManager.getLaunchIntentForPackage(
                    packageName
                )!!
            )()
        } catch (e: Exception) {
            e.printStackTrace()
            toaster.toast(R.string.es_activity_not_found)
        }
    }
}

@Transient
internal class AppActionFactory(
    private val appStore: AppStore,
    private val appActionExecutorProvider: @Provider (String) -> AppActionExecutor,
    private val appActionIconProvider: @Provider (String) -> AppActionIconProvider
) : ActionFactory {
    override fun handles(key: String): Boolean = key.startsWith(ACTION_KEY_PREFIX)
    override suspend fun createAction(key: String): Action {
        val packageName = key.removePrefix(ACTION_KEY_PREFIX)
        return Action(
            key = key,
            title = appStore.getAppInfo(packageName).packageName,
            unlockScreen = true,
            iconProvider = appActionIconProvider(packageName),
            executor = appActionExecutorProvider(packageName),
            enabled = true
        )
    }
}

@Transient
internal class AppActionPickerDelegate(
    private val launchableAppFilter: LaunchableAppFilter,
    private val resourceProvider: ResourceProvider
) : ActionPickerDelegate {
    override val title: String
        get() = resourceProvider.getString(R.string.es_action_app)
    override val icon: @Composable () -> Unit
        get() = { Icon(Icons.Default.Apps) }

    override suspend fun getResult(navigator: Navigator): ActionPickerResult? {
        val app = navigator.push<AppInfo>(
            AppPickerRoute(appFilter = launchableAppFilter)
        ) ?: return null
        return ActionPickerResult.Action("$ACTION_KEY_PREFIX${app.packageName}")
    }
}

@Transient
internal class AppActionIconProvider(
    private val delegateProvider: @Provider (Any) -> CoilActionIconProvider,
    private val packageName: @Assisted String
) : ActionIconProvider {
    override val icon: Flow<@Composable () -> Unit>
        get() = delegateProvider(AppIcon(packageName)).icon
}

private const val ACTION_KEY_PREFIX = "app=:="
