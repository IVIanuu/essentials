/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_DISMISS_NOTIFICATION_SHADE
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.material.Icon
import androidx.compose.material.contentColorFor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import arrow.core.Either
import coil.compose.rememberAsyncImagePainter
import com.ivianuu.essentials.AppConfig
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.accessibility.AccessibilityService
import com.ivianuu.essentials.accessibility.AndroidAccessibilityService
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.ui.LocalActionIconSizeModifier
import com.ivianuu.essentials.gestures.action.ui.LocalActionImageSizeModifier
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey

fun staticActionImage(data: Any) = ActionIcon {
  Image(
    painter = rememberAsyncImagePainter(data),
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

operator fun TypeKey<Permission>.plus(other: TypeKey<Permission>) = listOf(this, other)

fun interface ActionIntentSender {
  operator fun invoke(intent: Intent, options: Bundle?)
}

@Provide fun actionIntentSender(
  appContext: AppContext,
  toaster: Toaster
) = ActionIntentSender { intent, options ->
  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  Either.catch {
    PendingIntent.getActivity(
      appContext,
      1000,
      intent,
      PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE,
      options
    ).send()
  }.onLeft {
    it.printStackTrace()
    toaster(R.string.activity_not_found)
  }
}

fun interface CloseSystemDialogsUseCase {
  suspend operator fun invoke(): Either<Throwable, Unit>
}

@SuppressLint("MissingPermission", "InlinedApi")
@Provide
fun closeSystemDialogsUseCase(
  appConfig: AppConfig,
  appContext: AppContext,
  accessibilityService: AccessibilityService,
) = CloseSystemDialogsUseCase {
  Either.catch {
    if (appConfig.sdk >= 31)
      accessibilityService.performGlobalAction(GLOBAL_ACTION_DISMISS_NOTIFICATION_SHADE)
    else
      appContext.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
  }
}
