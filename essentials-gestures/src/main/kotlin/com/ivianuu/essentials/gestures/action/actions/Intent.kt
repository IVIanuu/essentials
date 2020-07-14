package com.ivianuu.essentials.gestures.action.actions

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.ivianuu.essentials.app.applicationContext
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader

@Reader
@Given
internal class IntentActionExecutor(
    private val intent: Intent
) : ActionExecutor {
    override suspend fun invoke() {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            PendingIntent.getActivity(
                applicationContext, 99, intent, 0, null
            ).send()
        } catch (e: Exception) {
            e.printStackTrace()
            Toaster.toast(R.string.es_activity_not_found)
        }
    }
}
