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

import android.view.KeyEvent
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.gestures.action.ActionSettingsKey
import com.ivianuu.essentials.loadResource
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.injekt.Provide

@Provide object SkipNextActionId : ActionId("media_skip_next")

@Provide fun skipNextMediaAction(RP: ResourceProvider) = Action(
  id = SkipNextActionId,
  title = loadResource(R.string.es_action_media_skip_next),
  icon = staticActionIcon(R.drawable.es_ic_skip_next)
)

@Provide fun skipNextMediaActionExecutor(
  mediaActionSender: MediaActionSender
): ActionExecutor<SkipNextActionId> = {
  mediaActionSender(KeyEvent.KEYCODE_MEDIA_NEXT)
}

@Provide inline val skipNextMediaActionSettingsKey: @ActionSettingsKey<SkipNextActionId> Key<Unit>
  get() = MediaActionSettingsKey()
