/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.*
import android.annotation.*
import android.app.*
import android.content.*
import android.os.*
import androidx.compose.foundation.*
import androidx.compose.material.*
import androidx.compose.ui.graphics.vector.*
import coil.compose.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.floatingwindows.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.gestures.action.ui.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.shell.*
import com.ivianuu.essentials.util.*

fun staticActionImage(data: Any) = ActionIcon {
  Image(
    painter = rememberImagePainter(data),
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

fun interface ActionRootCommandRunner : suspend (String) -> Unit

@Provide fun actionRootCommandRunner(
  shell: Shell,
  T: ToastContext
) = ActionRootCommandRunner { command ->
  runCatching { shell.run(command) }
    .onFailure {
      it.printStackTrace()
      showToast(R.string.es_no_root)
    }
}

fun interface ActionIntentSender : (Intent, Boolean, Bundle?) -> Unit

@Provide fun actionIntentSender(
  context: AppContext,
  T: ToastContext
) = ActionIntentSender { intent, isFloating, options ->
  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  if (isFloating)
    intent.addFlags(FLOATING_WINDOW_FLAG)
  runCatching {
    PendingIntent.getActivity(
      context, 1000, intent, PendingIntent.FLAG_CANCEL_CURRENT, options
    ).send()
  }.onFailure {
    it.printStackTrace()
    showToast(R.string.es_activity_not_found)
  }
}

fun interface CloseSystemDialogsUseCase : suspend () -> Result<Unit, Throwable>

@SuppressLint("MissingPermission", "InlinedApi")
@Provide
fun closeSystemDialogsUseCase(
  context: AppContext,
  globalActionExecutor: GlobalActionExecutor,
  systemBuildInfo: SystemBuildInfo
) = CloseSystemDialogsUseCase {
  runCatching {
    if (systemBuildInfo.sdk >= 31)
      globalActionExecutor(AccessibilityService.GLOBAL_ACTION_DISMISS_NOTIFICATION_SHADE)
    else
      context.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
  }
}
