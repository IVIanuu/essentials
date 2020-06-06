package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.provider.MediaStore
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.PhotoCamera
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.action
import com.ivianuu.essentials.util.ResourceProvider
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.StringKey
import com.ivianuu.injekt.composition.installIn

@Module
private fun CameraModule() {
    installIn<ApplicationComponent>()
    action { resourceProvider: ResourceProvider,
             executorFactory: @Provider (Intent) -> IntentActionExecutor ->
        Action(
            key = "camera",
            title = resourceProvider.getString(R.string.es_action_camera),
            iconProvider = SingleActionIconProvider(Icons.Default.PhotoCamera),
            executor = executorFactory(Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)),
            unlockScreen = true
        ) as @StringKey("camera") Action
    }
}
