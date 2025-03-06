/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService.*
import android.annotation.*
import android.app.*
import android.content.*
import android.os.*
import arrow.core.*
import essentials.*
import essentials.accessibility.*
import essentials.util.*
import injekt.*

@Provide class ActionIntentSender(
  private val appContext: AppContext,
  private val toaster: Toaster
) {
  fun sendIntent(intent: Intent, options: Bundle?) {
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    catch {
      PendingIntent.getActivity(
        appContext,
        1000,
        intent,
        PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        options
      ).send()
    }.onLeft {
      it.printStackTrace()
      toaster.toast("Failed to launch screen!")
    }
  }
}

@Provide class SystemDialogController(
  private val appConfig: AppConfig,
  private val appContext: AppContext,
  private val accessibilityManager: AccessibilityManager,
) {
  @SuppressLint("MissingPermission", "InlinedApi")
  suspend fun closeSystemDialogs(): Either<Throwable, Unit> = catch {
    if (appConfig.sdk >= 31)
      accessibilityManager.performGlobalAction(GLOBAL_ACTION_DISMISS_NOTIFICATION_SHADE)
    else
      appContext.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
  }
}
