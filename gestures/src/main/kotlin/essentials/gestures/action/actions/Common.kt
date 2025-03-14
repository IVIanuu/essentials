
/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService.*
import android.annotation.*
import android.app.*
import android.content.*
import android.os.*
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.onFailure
import essentials.*
import essentials.accessibility.*
import essentials.gestures.action.ActionAccessibilityPermission
import essentials.util.*
import injekt.*

val accessibilityActionPermissions = listOf(ActionAccessibilityPermission::class)

suspend fun sendActionIntent(
  intent: Intent,
  options: Bundle?,
  scope: Scope<*> = inject
) {
  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  catch {
    PendingIntent.getActivity(
      appContext(),
      1000,
      intent,
      PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE,
      options
    ).send()
  }.onFailure {
    it.printStackTrace()
    showToast("Failed to launch screen!")
  }
}

@SuppressLint("MissingPermission", "InlinedApi")
suspend fun closeSystemDialogs(scope: Scope<*> = inject) = catch {
  if (appConfig().sdk >= 31)
    performGlobalAccessibilityAction(GLOBAL_ACTION_DISMISS_NOTIFICATION_SHADE)
  else
    appContext().sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
}
