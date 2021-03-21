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

import android.view.KeyEvent
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionExecutorBinding
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.gestures.action.ActionSettingsKeyBinding
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.Given

@Given
object SkipPreviousActionId : ActionId("media_skip_previous")

@Given
fun skipPreviousMediaAction(
    @Given resourceProvider: ResourceProvider
): @ActionBinding<SkipPreviousActionId> Action = Action(
    id = SkipPreviousActionId,
    title = resourceProvider.string(R.string.es_action_media_skip_previous),
    icon = singleActionIcon(R.drawable.es_ic_skip_previous)
)

@Given
fun skipPreviousMediaActionExecutor(
    @Given mediaActionSender: MediaActionSender
): @ActionExecutorBinding<SkipPreviousActionId> ActionExecutor = {
    mediaActionSender(KeyEvent.KEYCODE_MEDIA_PREVIOUS)
}

@Given
inline val skipPreviousMediaActionSettingsKey:
        @ActionSettingsKeyBinding<SkipPreviousActionId> MediaActionSettingsKey
    get() = MediaActionSettingsKey()