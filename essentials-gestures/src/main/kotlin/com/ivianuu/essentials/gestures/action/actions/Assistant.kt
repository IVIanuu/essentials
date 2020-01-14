package com.ivianuu.essentials.gestures.action.actions

/**
import android.annotation.SuppressLint
import android.os.Bundle
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.data.Flag

private fun createAssistantAction() = Action(
    key = KEY_ASSISTANT,
    title = string(R.string.action_assistant),
    states = stateless(R.drawable.es_ic_google),
    flags = setOf(Flag.UnlockScreen)
)

@SuppressLint("DiscouragedPrivateApi")
private fun launchAssistant() {
    try {
        val launchAssist = searchManager.javaClass
            .getDeclaredMethod("launchAssist", Bundle::class.java)
        launchAssist.invoke(searchManager, Bundle())
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
*/