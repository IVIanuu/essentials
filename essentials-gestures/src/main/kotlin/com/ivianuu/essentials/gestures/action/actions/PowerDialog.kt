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
import com.ivianuu.essentials.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.injekt.*

@Given
object PowerDialogActionId : ActionId("power_dialog")

@Given
fun powerDialogAction(
    @Given stringResource: StringResourceProvider
) = Action<PowerDialogActionId>(
    id = PowerDialogActionId,
    title = stringResource(R.string.es_action_power_dialog, emptyList()),
    permissions = accessibilityActionPermissions,
    icon = singleActionIcon(R.drawable.es_ic_power_settings_new)
)

@Given
fun powerDialogActionExecutor(
    @Given globalActionExecutor: GlobalActionExecutor
): ActionExecutor<PowerDialogActionId> = {
    globalActionExecutor(AccessibilityService.GLOBAL_ACTION_POWER_DIALOG)
}
