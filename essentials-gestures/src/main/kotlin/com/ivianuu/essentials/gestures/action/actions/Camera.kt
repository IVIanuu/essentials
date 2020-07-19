package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.provider.MediaStore
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.PhotoCamera
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.BindAction
import com.ivianuu.essentials.util.Resources
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.SetElements
import com.ivianuu.injekt.given

@BindAction
@Reader
fun cameraAction() = Action(
    key = "camera",
    title = Resources.getString(R.string.es_action_camera),
    iconProvider = SingleActionIconProvider(Icons.Default.PhotoCamera),
    executor = given<IntentActionExecutor>(Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)),
    unlockScreen = true
)

