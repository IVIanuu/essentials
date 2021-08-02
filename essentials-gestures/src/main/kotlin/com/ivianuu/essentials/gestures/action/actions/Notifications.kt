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

import android.accessibilityservice.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.injekt.*

@Provide object NotificationsActionId : ActionId("notifications")

@Provide fun notificationsAction(rp: ResourceProvider): Action<NotificationsActionId> = Action(
  id = NotificationsActionId,
  title = loadResource(R.string.es_action_notifications),
  permissions = accessibilityActionPermissions,
  icon = singleActionIcon(Icons.Default.Notifications)
)

@Provide fun notificationsActionExecutor(
  globalActionExecutor: GlobalActionExecutor
): ActionExecutor<NotificationsActionId> = {
  globalActionExecutor(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS)
}
