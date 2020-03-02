package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.ui.graphics.vector.VectorAsset
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.bindAction
import com.ivianuu.essentials.icon.Essentials
import com.ivianuu.essentials.icon.EssentialsIcons
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.lazyMaterialIcon
import androidx.ui.graphics.vector.path
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Lazy
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.parametersOf

val EssentialsIcons.ActionHome: VectorAsset by lazyMaterialIcon {
    path {
        moveTo(12.0f, 19.33f)
        arcToRelative(7.33f, 7.33f, 0.0f, true, false, 0.0f, -14.66f)
        arcToRelative(7.33f, 7.33f, 0.0f, false, false, 0.0f, 14.66f)
        close()
        moveToRelative(0.0f, 3.0f)
        arcToRelative(10.33f, 10.33f, 0.0f, true, true, 0.0f, -20.66f)
        arcToRelative(10.33f, 10.33f, 0.0f, false, true, 0.0f, 20.66f)
        close()
    }
}

internal val EsHomeActionModule = Module {
    if (Build.MANUFACTURER != "OnePlus" || Build.MODEL == "GM1913") {
        bindAccessibilityAction(
            key = "home",
            accessibilityAction = AccessibilityService.GLOBAL_ACTION_HOME,
            titleRes = R.string.es_action_home,
            icon = Icons.Essentials.ActionHome
        )
    } else {
        bindAction(
            key = "home",
            title = { getStringResource(R.string.es_action_home) },
            iconProvider = { SingleActionIconProvider(Icons.Essentials.ActionHome) },
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
