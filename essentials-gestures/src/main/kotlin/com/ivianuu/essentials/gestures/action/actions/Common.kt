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

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.Image
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import coil.compose.rememberImagePainter
import com.github.michaelbull.result.onFailure
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.floatingwindows.FLOATING_WINDOW_FLAG
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.ui.LocalActionIconSizeModifier
import com.ivianuu.essentials.gestures.action.ui.LocalActionImageSizeModifier
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.shell.RunShellCommandUseCase
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
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
    painterResId = id = id,
    modifier = LocalActionIconSizeModifier.current
  )
}

operator fun TypeKey<Permission>.plus(other: TypeKey<Permission>) = listOf(this, other)

typealias ActionRootCommandRunner = suspend (String) -> Unit

@Provide fun actionRootCommandRunner(
  runShellCommand: RunShellCommandUseCase,
  rp: ResourceProvider,
  toaster: Toaster
): ActionRootCommandRunner = { command ->
  catch { runShellCommand(listOf(command)) }
    .onFailure {
      it.printStackTrace()
      showToast(R.string.es_no_root)
    }
}

typealias ActionIntentSender = (Intent, Boolean, Bundle?) -> Unit

@Provide fun actionIntentSender(
  context: AppContext,
  rp: ResourceProvider,
  toaster: Toaster
): ActionIntentSender = { intent, isFloating, options ->
  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
  if (isFloating)
    intent.addFlags(FLOATING_WINDOW_FLAG)
  catch {
    PendingIntent.getActivity(
      context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT, options
    ).send()
  }.onFailure {
    it.printStackTrace()
    showToast(R.string.es_activity_not_found)
  }
}
