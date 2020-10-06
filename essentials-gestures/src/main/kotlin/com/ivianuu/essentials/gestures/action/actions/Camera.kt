package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.provider.MediaStore
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionBinding
import com.ivianuu.essentials.util.Resources

@ActionBinding
fun cameraAction(
    resources: Resources,
    sendIntent: sendIntent,
) = Action(
    key = "camera",
    title = resources.getString(R.string.es_action_camera),
    icon = singleActionIcon(R.drawable.es_ic_photo_camera),
    unlockScreen = true,
    execute = { sendIntent(Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)) }
)
