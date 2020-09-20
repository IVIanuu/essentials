package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Build
import com.ivianuu.essentials.app.androidApplicationContext
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.GivenAction
import com.ivianuu.essentials.gestures.action.permissions
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.Reader

private val needsHomeIntentWorkaround = Build.MANUFACTURER != "OnePlus" || Build.MODEL == "GM1913"

@GivenAction
fun homeAction() = Action(
    key = "home",
    title = Resources.getString(R.string.es_action_home),
    permissions = permissions {
        if (needsHomeIntentWorkaround) emptyList()
        else listOf(accessibility)
    },
    icon = singleActionIcon(R.drawable.es_ic_action_home),
    execute = {
        if (needsHomeIntentWorkaround) openHomeScreen()
        else performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)
    }
)

@Reader
private fun openHomeScreen() {
    try {
        val intent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
        androidApplicationContext.sendBroadcast(intent)
    } catch (t: Throwable) {
        t.printStackTrace()
    }

    Intent(Intent.ACTION_MAIN).apply {
        addCategory(
            Intent.CATEGORY_HOME
        )
    }.send()
}
