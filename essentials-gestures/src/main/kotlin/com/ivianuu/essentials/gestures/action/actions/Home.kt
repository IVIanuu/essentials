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
import com.github.michaelbull.result.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*

@Given object HomeActionId : ActionId("home")

@Given fun homeAction(
  @Given stringResource: StringResourceProvider
) = Action<HomeActionId>(
  id = HomeActionId,
  title = stringResource(R.string.es_action_home, emptyList()),
  permissions = if (needsHomeIntentWorkaround) emptyList()
  else accessibilityActionPermissions,
  icon = singleActionIcon(R.drawable.es_ic_action_home)
)

@Given fun homeActionExecutor(
  @Given actionIntentSender: ActionIntentSender,
  @Given appContext: AppContext,
  @Given globalActionExecutor: GlobalActionExecutor,
): ActionExecutor<HomeActionId> = {
  if (!needsHomeIntentWorkaround) {
    globalActionExecutor(AccessibilityService.GLOBAL_ACTION_HOME)
  } else {
    catch {
      val intent = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
      appContext.sendBroadcast(intent)
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
