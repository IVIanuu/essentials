/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action.actions

import android.provider.*
import androidx.compose.material3.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.collectAsState
import essentials.compose.*
import essentials.data.*
import essentials.gestures.action.*
import injekt.*

@Provide object AutoRotationActionId : ActionId("auto_rotation") {
  @Provide fun action(autoRotationDataStore: DataStore<AutoRotation>) = Action(
    id = AutoRotationActionId,
    title = "Auto rotation",
    permissions = listOf(ActionWriteSettingsPermission::class),
    icon = {
      Icon(
        if (autoRotationDataStore.data.collectAsState(1).value == 1) Icons.Default.ScreenRotation
        else Icons.Default.ScreenLockRotation,
        null
      )
    }
  )

  @Provide fun executor(
    autoRotationDataStore: DataStore<AutoRotation>,
  ) = ActionExecutor<AutoRotationActionId> {
    autoRotationDataStore.updateData { if (this != 1) 1 else 0 }
  }
}

@Tag @Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS, AnnotationTarget.CONSTRUCTOR)
annotation class AutoRotationTag
internal typealias AutoRotation = @AutoRotationTag Int

@Provide val autoRotationModule = AndroidSettingModule<AutoRotation>(
  Settings.System.ACCELEROMETER_ROTATION, AndroidSettingsType.SYSTEM, 1
)
