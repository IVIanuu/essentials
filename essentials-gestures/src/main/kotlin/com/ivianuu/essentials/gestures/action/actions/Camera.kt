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

import android.content.Intent
import android.provider.MediaStore
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.gestures.action.ActionExecutorBinding
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.util.stringResource
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenFun

@Given object CameraActionId : ActionId("camera")

@ActionBinding<CameraActionId>
@Given
fun cameraAction(@Given stringResource: stringResource): Action = Action(
    id = CameraActionId,
    title = stringResource(R.string.es_action_camera),
    icon = singleActionIcon(R.drawable.es_ic_photo_camera),
    unlockScreen = true
)

@ActionExecutorBinding<CameraActionId>
@GivenFun
suspend fun openCamera(sendIntent: sendIntent) {
    sendIntent(Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA))
}
