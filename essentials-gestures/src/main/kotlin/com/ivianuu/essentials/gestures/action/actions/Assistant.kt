package com.ivianuu.essentials.gestures.action.actions

import android.annotation.SuppressLint
import android.app.SearchManager
import android.os.Bundle
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.BindAction
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given

@BindAction
@Reader
fun assistantAction() = Action(
    key = "assistant",
    title = Resources.getString(R.string.es_action_assistant),
    iconProvider = SingleActionIconProvider(R.drawable.es_ic_google),
    unlockScreen = true,
    executor = given<AssistantActionExecutor>()
)

@Given
internal class AssistantActionExecutor : ActionExecutor {
    @SuppressLint("DiscouragedPrivateApi")
    override suspend fun invoke() {
        try {
            val searchManager = given<SearchManager>()
            val launchAssist = searchManager.javaClass
                .getDeclaredMethod("launchAssist", Bundle::class.java)
            launchAssist.invoke(searchManager, Bundle())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
