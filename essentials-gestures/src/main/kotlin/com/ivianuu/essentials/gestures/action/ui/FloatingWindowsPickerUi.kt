/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import com.ivianuu.essentials.apps.*
import com.ivianuu.essentials.floatingwindows.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.state.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.layout.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

data class FloatingWindowsPickerKey(val actionTitle: String) : Key<Boolean>

@Provide fun floatingWindowsPickerUi(
  commonStrings: CommonStrings
) = ModelKeyUi<FloatingWindowsPickerKey, FloatingWindowsPickerModel> {
  Scaffold(
    topBar = { TopAppBar(title = { Text(R.string.es_floating_window_picker_title) }) }
  ) {
    Column {
      Text(
        modifier = Modifier.padding(16.dp),
        text = stringResource(R.string.es_floating_window_picker_content, model.actionTitle),
        style = MaterialTheme.typography.body2
      )

      HorizontalDivider()

      Text(
        modifier = Modifier
          .height(48.dp)
          .fillMaxWidth()
          .clickable(onClick = model.openFloatingWindow)
          .align(Alignment.CenterStart)
          .padding(horizontal = 16.dp),
        text = commonStrings.yes,
        style = MaterialTheme.typography.button
      )

      HorizontalDivider()

      Text(
        modifier = Modifier
          .height(48.dp)
          .fillMaxWidth()
          .clickable(onClick = model.openFullScreen)
          .align(Alignment.CenterStart)
          .padding(horizontal = 16.dp),
        text = commonStrings.no,
        style = MaterialTheme.typography.button
      )

      HorizontalDivider()
    }
  }
}

data class FloatingWindowsPickerModel(
  val actionTitle: String,
  val openFloatingWindow: () -> Unit,
  val openFullScreen: () -> Unit
)

@Provide @Composable fun floatingWindowsPickerModel(
  appRepository: AppRepository,
  T: ToastContext,
  ctx: KeyUiContext<FloatingWindowsPickerKey>
) = FloatingWindowsPickerModel(
  actionTitle = ctx.key.actionTitle,
  openFloatingWindow = action {
    if (appRepository.isAppInstalled(FLOATING_WINDOWS_PACKAGE).first()) {
      ctx.navigator.pop(ctx.key, true)
    } else {
      showToast(R.string.es_floating_windows_not_installed)
      ctx.navigator.push(
        PlayStoreAppDetailsKey(
          FLOATING_WINDOWS_PACKAGE
        )
      )
    }
  },
  openFullScreen = action {
    ctx.navigator.pop(ctx.key, false)
  }
)
