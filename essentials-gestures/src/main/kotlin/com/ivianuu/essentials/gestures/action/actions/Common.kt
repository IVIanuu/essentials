/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.material.Icon
import androidx.compose.ui.graphics.vector.ImageVector
import coil.compose.rememberAsyncImagePainter
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.Result
import com.ivianuu.essentials.SystemBuildInfo
import com.ivianuu.essentials.accessibility.GlobalActionExecutor
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.floatingwindows.FLOATING_WINDOW_FLAG
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.ui.LocalActionIconSizeModifier
import com.ivianuu.essentials.gestures.action.ui.LocalActionImageSizeModifier
import com.ivianuu.essentials.onFailure
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.shell.Shell
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.invoke
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.TypeKey
import com.ivianuu.injekt.inject

fun staticActionImage(data: Any) = ActionIcon {
  Image(
    painter = rememberAsyncImagePainter(data),
    modifier = LocalActionImageSizeModifier.current
  )
}

fun staticActionIcon(icon: ImageVector) = ActionIcon {
  Icon(
    imageVector = icon,
    modifier = LocalActionIconSizeModifier.current
  )
}

fun staticActionIcon(id: Int) = ActionIcon {
  Icon(
    painterResId = id,
    modifier = LocalActionIconSizeModifier.current
  )
}

operator fun TypeKey<Permission>.plus(other: TypeKey<Permission>) = listOf(this, other)

fun interface ActionRootCommandRunner {
  suspend operator fun invoke(command: String)
}

@Provide fun actionRootCommandRunner(
  shell: Shell,
  resourceProvider: ResourceProvider,
  toaster: Toaster
) = ActionRootCommandRunner { command ->
  shell.run(command)
    .onFailure {
      it.printStackTrace()
      toaster(R.string.es_no_root)
    }
}

fun interface ActionIntentSender {
  operator fun invoke(intent: Intent, isFloating: Boolean, options: Bundle?)
}

@Provide fun actionIntentSender(
  appContext: AppContext,
  resourceProvider: ResourceProvider,
  toaster: Toaster
) = ActionIntentSender { intent, isFloating, options ->
  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  if (isFloating)
    intent.addFlags(FLOATING_WINDOW_FLAG)
  catch {
    PendingIntent.getActivity(
      inject(),
      1000,
      intent,
      PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE,
      options
    ).send()
  }.onFailure {
    it.printStackTrace()
    toaster(R.string.es_activity_not_found)
  }
}

fun interface CloseSystemDialogsUseCase {
  suspend operator fun invoke(): Result<Unit, Throwable>
}

@SuppressLint("MissingPermission", "InlinedApi")
@Provide
fun closeSystemDialogsUseCase(
  appContext: AppContext,
  globalActionExecutor: GlobalActionExecutor,
  systemBuildInfo: SystemBuildInfo
) = CloseSystemDialogsUseCase {
  catch {
    if (systemBuildInfo.sdk >= 31)
      globalActionExecutor(AccessibilityService.GLOBAL_ACTION_DISMISS_NOTIFICATION_SHADE)
    else
      appContext.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
  }
}
