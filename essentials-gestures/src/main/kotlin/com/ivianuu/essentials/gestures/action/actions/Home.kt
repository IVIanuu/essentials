package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.ui.foundation.Icon
import androidx.ui.res.vectorResource
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.action
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Lazy
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.parametersOf

internal val EsHomeActionModule = Module {
    if (Build.MANUFACTURER != "OnePlus" || Build.MODEL == "GM1913") {
        bindAccessibilityAction(
            key = "home",
            accessibilityAction = AccessibilityService.GLOBAL_ACTION_HOME,
            titleRes = R.string.es_action_home,
            icon = { Icon(vectorResource(R.drawable.es_ic_action_home)) }
        )
    } else {
        action(
            key = "home",
            title = { getStringResource(R.string.es_action_home) },
            iconProvider = {
                SingleActionIconProvider {
                    Icon(vectorResource(R.drawable.es_ic_action_home))
                }
            },
            executor = { get<IntentHomeActionExecutor>() }
        )
    }
}

@Factory
internal class IntentHomeActionExecutor(
    private val context: Context,
    private val lazyDelegate: Lazy<IntentActionExecutor>
) : ActionExecutor {
    override suspend fun invoke() {
        try {
            val intent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            context.sendBroadcast(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        lazyDelegate(parameters = parametersOf(Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_HOME) }))()
    }
}
