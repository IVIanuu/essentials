package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.os.Build
import com.ivianuu.essentials.app.applicationContext
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.BindAction
import com.ivianuu.essentials.gestures.action.permissions
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given

private val needsHomeIntentWorkaround = Build.MANUFACTURER != "OnePlus" || Build.MODEL == "GM1913"

@BindAction
@Reader
fun homeAction() = Action(
    key = "home",
    title = Resources.getString(R.string.es_action_home),
    permissions = permissions {
        if (needsHomeIntentWorkaround) emptyList()
        else listOf(accessibility)
    },
    iconProvider = SingleActionIconProvider(R.drawable.es_ic_action_home),
    executor = if (needsHomeIntentWorkaround) given<() -> IntentHomeActionExecutor>()()
    else given<(Int) -> AccessibilityActionExecutor>()(AccessibilityService.GLOBAL_ACTION_HOME)
)

@Given
@Reader
internal class IntentHomeActionExecutor : ActionExecutor {
    override suspend fun invoke() {
        try {
            val intent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            applicationContext.sendBroadcast(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        given<(Intent) -> IntentActionExecutor>()(Intent(Intent.ACTION_MAIN).apply {
            addCategory(
                Intent.CATEGORY_HOME
            )
        })()
    }
}
