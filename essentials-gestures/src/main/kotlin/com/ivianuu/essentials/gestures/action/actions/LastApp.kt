package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Repeat
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionPermissions
import com.ivianuu.essentials.gestures.action.action
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Lazy
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.StringKey
import com.ivianuu.injekt.Transient
import com.ivianuu.injekt.composition.installIn
import kotlinx.coroutines.delay


@Module
private fun LastAppModule() {
    installIn<ApplicationComponent>()
    action { resourceProvider: ResourceProvider,
             actionPermissions: ActionPermissions,
             executor: LastAppActionExecutor ->
        Action(
            key = "last_app",
            title = resourceProvider.getString(R.string.es_action_last_app),
            iconProvider = SingleActionIconProvider(Icons.Default.Repeat),
            permissions = listOf(actionPermissions.accessibility),
            executor = executor
        ) as @StringKey("last_app") Action
    }
    /*bindAction<@ActionQualifier("last_app") Action>(
        key = "last_app",
        title = { getStringResource(R.string.es_action_last_app) },
        iconProvider = { SingleActionIconProvider(Icons.Default.Repeat) },
        permissions = { listOf(actionPermission { accessibility }) },
        unlockScreen = { true },
        executor = { get<LastAppActionExecutor>() }
    )*/
}

@Transient
internal class LastAppActionExecutor(
    private val lazyRecentAppsExecutor: @Lazy() (Int) -> AccessibilityActionExecutor
) : ActionExecutor {
    override suspend fun invoke() {
        val executor =
            lazyRecentAppsExecutor(AccessibilityService.GLOBAL_ACTION_RECENTS)
        executor()
        delay(250)
        executor()
    }
}
