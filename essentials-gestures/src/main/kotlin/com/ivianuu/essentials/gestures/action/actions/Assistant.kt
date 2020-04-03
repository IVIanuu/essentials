package com.ivianuu.essentials.gestures.action.actions

import android.annotation.SuppressLint
import android.app.SearchManager
import android.os.Bundle
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.action
import com.ivianuu.injekt.ApplicationScope
import com.ivianuu.injekt.ComponentBuilder
import com.ivianuu.injekt.Factory
import com.ivianuu.injekt.Module

@ApplicationScope
@Module
private fun ComponentBuilder.assistantAction() {
    action(
        key = "assistant",
        title = { getStringResource(R.string.es_action_assistant) },
        iconProvider = { SingleActionIconProvider(R.drawable.es_ic_google) },
        unlockScreen = { true },
        executor = { get<AssistantActionExecutor>() }
    )
}

@Factory
private class AssistantActionExecutor(
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
