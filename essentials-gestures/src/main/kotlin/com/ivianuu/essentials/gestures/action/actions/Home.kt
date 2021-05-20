/*
 * Copyright 2020 Manuel Wrage
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

import android.accessibilityservice.*
import android.content.*
import android.os.*
import androidx.compose.ui.res.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*

@Provide object HomeActionId : ActionId("home")

@Provide fun homeAction(resourceProvider: ResourceProvider): Action<HomeActionId> = Action(
  id = HomeActionId,
  title = resourceProvider(R.string.es_action_home),
  permissions = if (needsHomeIntentWorkaround) emptyList()
  else accessibilityActionPermissions,
  icon = singleActionIcon(R.drawable.es_ic_action_home)
)

@Provide fun homeActionExecutor(
  actionIntentSender: ActionIntentSender,
  context: AppContext,
  globalActionExecutor: GlobalActionExecutor,
): ActionExecutor<HomeActionId> = {
  if (!needsHomeIntentWorkaround) {
    globalActionExecutor(AccessibilityService.GLOBAL_ACTION_HOME)
  } else {
    catch {
      val intent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
      context.sendBroadcast(intent)
    }.onFailure { it.printStackTrace() }

    actionIntentSender(
      Intent(Intent.ACTION_MAIN).apply {
        addCategory(
          Intent.CATEGORY_HOME
        )
      }
    )
  }
}

private val needsHomeIntentWorkaround = Build.MANUFACTURER != "OnePlus" || Build.MODEL == "GM1913"
