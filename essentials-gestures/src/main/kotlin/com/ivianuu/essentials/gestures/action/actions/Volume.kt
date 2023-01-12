/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.media.AudioManager
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionExecutor
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.injekt.Provide

@Provide object VolumeActionId : ActionId("volume")

context(ResourceProvider) @Provide fun volumeAction() = Action(
  id = VolumeActionId,
  title = loadResource(R.string.es_action_volume),
  icon = staticActionIcon(R.drawable.es_ic_volume_up)
)

context(AudioManager)
    @Provide fun volumeActionExecutor() = ActionExecutor<VolumeActionId> {
  adjustStreamVolume(
    AudioManager.STREAM_MUSIC,
    AudioManager.ADJUST_SAME,
    AudioManager.FLAG_SHOW_UI
  )
}
