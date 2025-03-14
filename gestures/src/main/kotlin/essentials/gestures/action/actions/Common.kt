
/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService.*
import android.annotation.*
import android.app.*
import android.content.*
import android.os.*
import com.github.michaelbull.result.*
import essentials.*
import essentials.accessibility.*
import essentials.util.*
import injekt.*

@Tag typealias sendActionIntent = (Intent, Bundle?) -> Unit

@Provide fun sendActionIntent(
  appContext: AppContext,
  showToast: showToast
): sendActionIntent = { intent, options ->
  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  catch {
    PendingIntent.getActivity(
      appContext,
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

@Tag typealias closeSystemDialogsResult = Result<Unit, Throwable>
typealias closeSystemDialogs = suspend () -> closeSystemDialogsResult

@SuppressLint("MissingPermission", "InlinedApi")
@Provide suspend fun closeSystemDialogs(
  appConfig: AppConfig,
  appContext: AppContext,
  performAccessibilityAction: performGlobalAccessibilityAction
): closeSystemDialogsResult = catch {
  if (appConfig.sdk >= 31)
    performAccessibilityAction(GLOBAL_ACTION_DISMISS_NOTIFICATION_SHADE)
  else
    appContext.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
}
