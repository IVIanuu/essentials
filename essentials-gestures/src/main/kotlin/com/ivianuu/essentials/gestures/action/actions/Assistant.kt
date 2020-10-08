package com.ivianuu.essentials.gestures.action.actions

import android.app.SearchManager
import android.os.Bundle
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.util.stringResource

@ActionBinding
fun assistantAction(
    stringResource: stringResource,
    searchManager: SearchManager,
): Action = Action(
    key = "assistant",
    title = stringResource(R.string.es_action_assistant),
    unlockScreen = true,
    icon = singleActionIcon(R.drawable.es_ic_google),
    execute = {
        val launchAssist = searchManager.javaClass
            .getDeclaredMethod("launchAssist", Bundle::class.java)
        launchAssist.invoke(searchManager, Bundle())
    }
)
