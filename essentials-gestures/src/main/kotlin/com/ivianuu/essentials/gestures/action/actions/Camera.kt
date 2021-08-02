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

import android.content.*
import android.provider.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.gestures.*
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.injekt.*

@Provide object CameraActionId : ActionId("camera")

@Provide fun cameraAction(rp: ResourceProvider): Action<CameraActionId> = Action(
  id = CameraActionId,
  title = loadResource(R.string.es_action_camera),
  icon = singleActionIcon(R.drawable.es_ic_photo_camera),
  unlockScreen = true
)

@Provide fun cameraActionExecutor(
  actionIntentSender: ActionIntentSender
): ActionExecutor<CameraActionId> = {
  actionIntentSender(Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA))
}
