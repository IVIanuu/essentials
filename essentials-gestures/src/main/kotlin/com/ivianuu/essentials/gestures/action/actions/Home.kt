package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Build
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.gestures.action.choosePermissions
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.android.ApplicationContext

private val needsHomeIntentWorkaround = Build.MANUFACTURER != "OnePlus" || Build.MODEL == "GM1913"

@ActionBinding
fun homeAction(
    choosePermissions: choosePermissions,
    openHomeScreen: openHomeScreen,
    performGlobalAction: performGlobalAction,
    resources: Resources,
): Action = Action(
    key = "home",
    title = resources.getString(R.string.es_action_home),
    permissions = choosePermissions {
        if (needsHomeIntentWorkaround) emptyList()
        else listOf(accessibility)
    },
    icon = singleActionIcon(R.drawable.es_ic_action_home),
    execute = {
        if (needsHomeIntentWorkaround) openHomeScreen()
        else performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)
    }
)

@FunBinding
fun openHomeScreen(
    applicationContext: ApplicationContext,
    sendIntent: sendIntent,
) {
    try {
        val intent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        applicationContext.sendBroadcast(intent)
    } catch (t: Throwable) {
        t.printStackTrace()
    }

    sendIntent(
        Intent(Intent.ACTION_MAIN).apply {
            addCategory(
                Intent.CATEGORY_HOME
            )
        }
    )
}
