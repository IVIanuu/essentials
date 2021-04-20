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
import android.annotation.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.accessibility.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*

@Given
object LockScreenActionId : ActionId("lock_screen")

@Given
fun lockScreenAction(
    @Given stringResource: StringResourceProvider,
    @Given systemBuildInfo: SystemBuildInfo,
) = Action<LockScreenActionId>(
    id = LockScreenActionId,
    title = stringResource(R.string.es_action_lock_screen, emptyList()),
    icon = singleActionIcon(R.drawable.es_ic_power_settings),
    permissions = listOf(
        if (systemBuildInfo.sdk >= 28) typeKeyOf<ActionAccessibilityPermission>()
        else typeKeyOf<ActionRootPermission>()
    )
)

@SuppressLint("InlinedApi")
@Given
fun lockScreenActionExecutor(
    @Given actionRootCommandRunner: ActionRootCommandRunner,
    @Given globalActionExecutor: GlobalActionExecutor,
    @Given systemBuildInfo: SystemBuildInfo,
): ActionExecutor<LockScreenActionId> = {
    if (systemBuildInfo.sdk >= 28) {
        globalActionExecutor(AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN)
    } else {
        actionRootCommandRunner("input keyevent 26")
    }
}
