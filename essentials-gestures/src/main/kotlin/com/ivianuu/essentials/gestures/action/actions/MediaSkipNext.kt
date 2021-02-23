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
object SkipNextActionId : ActionId("media_skip_next")

@ActionBinding<SkipNextActionId>
@Given
fun skipNextMediaAction(@Given resourceProvider: ResourceProvider) = Action(
    id = SkipNextActionId,
    title = resourceProvider.string(R.string.es_action_media_skip_next),
    icon = singleActionIcon(R.drawable.es_ic_skip_next)
)

@ActionExecutorBinding<SkipNextActionId>
@Given
fun skipNextMediaActionExecutor(@Given mediaActionSender: MediaActionSender): ActionExecutor = {
    mediaActionSender(KeyEvent.KEYCODE_MEDIA_NEXT)
}

@ActionSettingsKeyBinding<SkipNextActionId>
@Given
inline val skipNextMediaActionSettingsKey: MediaActionSettingsKey
    get() = MediaActionSettingsKey()
