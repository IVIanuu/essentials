package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.provider.MediaStore
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.PhotoCamera
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.GivenAction
import com.ivianuu.essentials.util.Resources

@GivenAction
fun cameraAction() = Action(
    key = "camera",
    title = Resources.getString(R.string.es_action_camera),
    icon = singleActionIcon(Icons.Default.PhotoCamera),
    unlockScreen = true,
    execute = {
        Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
            .send()
    }
)
