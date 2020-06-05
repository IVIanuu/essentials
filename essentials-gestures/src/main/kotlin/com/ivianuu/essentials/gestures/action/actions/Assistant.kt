package com.ivianuu.essentials.gestures.action.actions

import android.annotation.SuppressLint
import android.app.SearchManager
import android.os.Bundle
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.action
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.StringKey
import com.ivianuu.injekt.Transient
import com.ivianuu.injekt.composition.installIn
import com.ivianuu.injekt.transient

@Module
private fun AssistantAction() {
    installIn<ApplicationComponent>()
    action { resourcesProvider: ResourceProvider, executor: AssistantActionExecutor ->
        Action(
            key = "assistant",
            title = resourcesProvider.getString(R.string.es_action_assistant),
            iconProvider = SingleActionIconProvider(R.drawable.es_ic_google),
            unlockScreen = true,
            executor = executor
        ) as @StringKey("assistant") Action
    }
}

@Transient
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
