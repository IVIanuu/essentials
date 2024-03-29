/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.provider.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.injekt.*

@Provide object AutoRotationActionId : ActionId("auto_rotation") {
  @Provide fun action(autoRotationDataStore: DataStore<AutoRotation>) = Action(
    id = AutoRotationActionId,
    title = "Auto rotation",
    permissions = listOf(ActionWriteSettingsPermission::class),
    icon = {
      Icon(
        if (autoRotationDataStore.data.state(1) == 1) Icons.Default.ScreenRotation
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
