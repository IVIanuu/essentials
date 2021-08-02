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

import android.view.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.gestures.*
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.injekt.*

@Provide object SkipNextActionId : ActionId("media_skip_next")

@Provide fun skipNextMediaAction(rp: ResourceProvider): Action<SkipNextActionId> = Action(
  id = SkipNextActionId,
  title = loadResource(R.string.es_action_media_skip_next),
  icon = singleActionIcon(R.drawable.es_ic_skip_next)
)

@Provide fun skipNextMediaActionExecutor(
  mediaActionSender: MediaActionSender
): ActionExecutor<SkipNextActionId> = {
  mediaActionSender(KeyEvent.KEYCODE_MEDIA_NEXT)
}

@Provide inline val skipNextMediaActionSettingsKey: MediaActionSettingsKey<SkipNextActionId>
  get() = MediaActionSettingsKey()
