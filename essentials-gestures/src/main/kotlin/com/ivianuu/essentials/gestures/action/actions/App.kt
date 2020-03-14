package com.ivianuu.essentials.gestures.action.actions

import android.content.pm.PackageManager
import androidx.compose.Composable
import androidx.ui.foundation.Icon
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
import com.ivianuu.essentials.gestures.action.bindActionFactoryIntoSet
import com.ivianuu.essentials.gestures.action.bindActionPickerDelegateIntoSet
import com.ivianuu.essentials.gestures.action.ui.picker.ActionPickerResult
import com.ivianuu.essentials.ui.navigation.NavigatorState
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Lazy
import com.ivianuu.injekt.Param
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.parametersOf
import kotlinx.coroutines.flow.Flow

internal fun ComponentBuilder.appAction() {
    bindActionFactoryIntoSet<AppActionFactory>()
    bindActionPickerDelegateIntoSet<AppActionPickerDelegate>()
}

@Factory
internal class AppActionExecutor(
    @Param private val packageName: String,
    private val packageManager: PackageManager,
    private val lazyDelegate: Lazy<IntentActionExecutor>,
    private val toaster: Toaster
) : ActionExecutor {
    override suspend fun invoke() {
        try {
            lazyDelegate(parameters = parametersOf({
                packageManager.getLaunchIntentForPackage(
                    packageName
                )
            }))()
        } catch (e: Exception) {
            e.printStackTrace()
            toaster.toast(R.string.es_activity_not_found)
        }
    }
}

@Factory
internal class AppActionFactory(
    private val appStore: AppStore,
    private val appActionExecutorProvider: Provider<AppActionExecutor>,
    private val appActionIconProvider: Provider<AppActionIconProvider>
) : ActionFactory {
    override fun handles(key: String): Boolean = key.startsWith(ACTION_KEY_PREFIX)
    override suspend fun createAction(key: String): Action {
        val packageName = key.removePrefix(ACTION_KEY_PREFIX)
        return Action(
            key = key,
            title = appStore.getAppInfo(packageName).packageName,
            unlockScreen = true,
            iconProvider = appActionIconProvider(parameters = parametersOf(packageName)),
            executor = appActionExecutorProvider(parameters = parametersOf(packageName))
        )
    }
}

@Factory
internal class AppActionPickerDelegate(
    private val launchableAppFilter: LaunchableAppFilter,
    private val resourceProvider: ResourceProvider
) : ActionPickerDelegate {
    override val title: String
        get() = resourceProvider.getString(R.string.es_action_app)
    override val icon: @Composable () -> Unit
        get() = { Icon(Icons.Default.Apps) }

    override suspend fun getResult(navigator: NavigatorState): ActionPickerResult? {
        val app = navigator.push<AppInfo>(
            AppPickerRoute(appFilter = launchableAppFilter)
        ) ?: return null
        return ActionPickerResult.Action("$ACTION_KEY_PREFIX${app.packageName}")
    }
}

@Factory
internal class AppActionIconProvider(
    private val lazyDelegate: Lazy<CoilActionIconProvider>,
    @Param private val packageName: String
) : ActionIconProvider {
    override val icon: Flow<@Composable () -> Unit>
        get() = lazyDelegate(parameters = parametersOf(AppIcon(packageName))).icon
}

private const val ACTION_KEY_PREFIX = "app=:="
