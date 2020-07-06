package com.ivianuu.essentials.gestures.action.actions

import android.annotation.SuppressLint
import android.app.SearchManager
import android.os.Bundle
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.bindAction
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.StringKey
import com.ivianuu.injekt.Unscoped
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.get
import com.ivianuu.injekt.unscoped

@Module
fun AssistantAction() {
    installIn<ApplicationComponent>()
    unscoped {
        Action(
            key = "assistant",
            title = Resources.getString(R.string.es_action_assistant),
            iconProvider = SingleActionIconProvider(R.drawable.es_ic_google),
            unlockScreen = true,
            executor = get<AssistantActionExecutor>()
        ) as @StringKey("assistant") Action
    }
    bindAction<@StringKey("assistant") Action>()
}

@Unscoped
internal class AssistantActionExecutor(
    private val searchManager: SearchManager
) : ActionExecutor {
    @SuppressLint("DiscouragedPrivateApi")
    override suspend fun invoke() {
        try {
            val launchAssist = searchManager.javaClass
                .getDeclaredMethod("launchAssist", Bundle::class.java)
            launchAssist.invoke(searchManager, Bundle())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
