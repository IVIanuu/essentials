/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import coil.compose.rememberImagePainter
import com.ivianuu.essentials.AppContext
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
import com.ivianuu.essentials.util.Toasts
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.common.TypeKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

fun singleActionImage(data: Any): Flow<ActionIcon> = flowOf {
  Image(
    painter = rememberImagePainter(data),
    modifier = LocalActionImageSizeModifier.current
  )
}

fun singleActionIcon(icon: @Composable () -> Unit): Flow<ActionIcon> = flowOf(icon)

fun singleActionIcon(icon: ImageVector) = singleActionIcon {
  Icon(
    imageVector = icon,
    modifier = LocalActionIconSizeModifier.current
  )
}

fun singleActionIcon(id: Int) = singleActionIcon {
  Icon(
    painterResId = id,
    modifier = LocalActionIconSizeModifier.current
  )
}

operator fun TypeKey<Permission>.plus(other: TypeKey<Permission>) = listOf(this, other)

@Tag annotation class ActionRootCommandRunnerTag
typealias ActionRootCommandRunner = @ActionRootCommandRunnerTag suspend (String) -> Unit

@Provide @Toasts fun actionRootCommandRunner(shell: Shell): ActionRootCommandRunner = { command ->
  catch { shell.run(command) }
    .onFailure {
      it.printStackTrace()
      showToast(R.string.es_no_root)
    }
}

@Tag annotation class ActionIntentSenderTag
typealias ActionIntentSender = @ActionIntentSenderTag (Intent, Boolean, Bundle?) -> Unit

@Provide @Toasts
fun actionIntentSender(context: AppContext): ActionIntentSender = { intent, isFloating, options ->
  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  if (isFloating)
    intent.addFlags(FLOATING_WINDOW_FLAG)
  catch {
    PendingIntent.getActivity(
      context, 1000, intent, PendingIntent.FLAG_CANCEL_CURRENT, options
    ).send()
  }.onFailure {
    it.printStackTrace()
    showToast(R.string.es_activity_not_found)
  }
}

@Tag annotation class CloseSystemDialogsUseCaseTag
typealias CloseSystemDialogsUseCase = @CloseSystemDialogsUseCaseTag suspend () -> Result<Unit, Throwable>

@SuppressLint("MissingPermission", "InlinedApi")
@Provide
fun closeSystemDialogsUseCase(
  context: AppContext,
  globalActionExecutor: GlobalActionExecutor,
  systemBuildInfo: SystemBuildInfo
): CloseSystemDialogsUseCase = {
  catch {
    if (systemBuildInfo.sdk >= 31)
      globalActionExecutor(AccessibilityService.GLOBAL_ACTION_DISMISS_NOTIFICATION_SHADE)
    else
      context.sendBroadcast(Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
  }
}
