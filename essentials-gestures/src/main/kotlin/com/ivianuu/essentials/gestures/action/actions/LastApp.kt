package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import android.content.Context
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.ForApplication
import com.ivianuu.injekt.Lazy
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Transient
import com.ivianuu.injekt.composition.installIn
import kotlinx.coroutines.delay

@Module
private fun LastAppModule() {
    installIn<ApplicationComponent>()
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
    private val context: @ForApplication Context,
    private val lazyRecentAppsExecutor: @Lazy (Int) -> AccessibilityActionExecutor
) : ActionExecutor {
    override suspend fun invoke() {
        val executor =
            lazyRecentAppsExecutor(AccessibilityService.GLOBAL_ACTION_RECENTS)
        executor()
        delay(250)
        executor()
    }
}
