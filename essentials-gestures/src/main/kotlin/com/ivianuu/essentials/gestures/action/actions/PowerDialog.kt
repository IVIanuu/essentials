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

import android.accessibilityservice.AccessibilityService
import com.ivianuu.essentials.accessibility.performGlobalAction
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.gestures.action.ActionExecutorBinding
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun

@Given object PowerDialogActionId : ActionId("power_dialog")

@ActionBinding<PowerDialogActionId>
fun powerDialogAction(@Given accessibilityAction: accessibilityAction): Action = accessibilityAction(
    PowerDialogActionId,
    R.string.es_action_power_dialog,
    singleActionIcon(R.drawable.es_ic_power_settings_new)
)

@ActionExecutorBinding<PowerDialogActionId>
@GivenFun
suspend fun showPowerDialog(@Given performGlobalAction: performGlobalAction) {
    performGlobalAction(AccessibilityService.GLOBAL_ACTION_POWER_DIALOG)
}
