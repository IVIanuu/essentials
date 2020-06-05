package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.os.Build
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionPermissions
import com.ivianuu.essentials.gestures.action.action
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.ForApplication
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.StringKey
import com.ivianuu.injekt.Transient
import com.ivianuu.injekt.composition.installIn

private val needsHomeIntentWorkaround = Build.MANUFACTURER != "OnePlus" || Build.MODEL == "GM1913"

@Module
private fun HomeModule() {
    installIn<ApplicationComponent>()
    action { resourceProvider: ResourceProvider,
             permissions: ActionPermissions,
             intentHomeExecutorFactory: @Provider () -> IntentHomeActionExecutor,
             accessibilityExecutorFactory: @Provider (Int) -> AccessibilityActionExecutor ->
        Action(
            key = "home",
            title = resourceProvider.getString(R.string.es_action_home),
            permissions = if (needsHomeIntentWorkaround) emptyList()
            else listOf(permissions.accessibility),
            iconProvider = SingleActionIconProvider(R.drawable.es_ic_action_home),
            executor = if (needsHomeIntentWorkaround) intentHomeExecutorFactory()
            else accessibilityExecutorFactory(AccessibilityService.GLOBAL_ACTION_HOME)
        ) as @StringKey("home") Action
    }
}

@Transient
internal class IntentHomeActionExecutor(
    private val context: @ForApplication Context,
    private val delegateProvider: @Provider (Intent) -> IntentActionExecutor
) : ActionExecutor {
    override suspend fun invoke() {
        try {
            val intent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            context.sendBroadcast(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        delegateProvider(Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_HOME) })()
    }
}
