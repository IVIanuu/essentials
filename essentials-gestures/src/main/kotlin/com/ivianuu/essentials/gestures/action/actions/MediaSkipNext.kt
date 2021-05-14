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

import android.view.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.gestures.*
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.injekt.*

@Given object SkipNextActionId : ActionId("media_skip_next")

@Given fun skipNextMediaAction(
  @Given stringResource: StringResourceProvider
) = Action<SkipNextActionId>(
  id = SkipNextActionId,
  title = stringResource(R.string.es_action_media_skip_next, emptyList()),
  icon = singleActionIcon(R.drawable.es_ic_skip_next)
)

@Given fun skipNextMediaActionExecutor(
  @Given mediaActionSender: MediaActionSender
): ActionExecutor<SkipNextActionId> = {
  mediaActionSender(KeyEvent.KEYCODE_MEDIA_NEXT)
}

@Given inline val skipNextMediaActionSettingsKey: MediaActionSettingsKey<SkipNextActionId>
  get() = MediaActionSettingsKey()
