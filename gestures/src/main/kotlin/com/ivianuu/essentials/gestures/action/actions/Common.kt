/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService.*
import android.annotation.*
import android.app.*
import android.content.*
import android.os.*
import androidx.compose.foundation.*
import androidx.compose.material3.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.res.*
import arrow.core.*
import coil.compose.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.gestures.action.ui.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*

fun staticActionImage(data: Any) = ActionIcon {
  AsyncImage(
    model = data,
    modifier = LocalActionImageSizeModifier.current,
    contentDescription = null
  )
}

fun staticActionIcon(icon: ImageVector) = ActionIcon {
  Icon(
    imageVector = icon,
    modifier = LocalActionIconSizeModifier.current,
    contentDescription = null
  )
}

fun staticActionIcon(id: Int) = ActionIcon {
  Icon(
    painter = painterResource(id),
    modifier = LocalActionIconSizeModifier.current,
    contentDescription = null
  )
}

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
