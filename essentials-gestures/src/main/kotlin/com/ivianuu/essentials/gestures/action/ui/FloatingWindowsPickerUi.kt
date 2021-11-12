package com.ivianuu.essentials.gestures.action.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.apps.AppRepository
import com.ivianuu.essentials.floatingwindows.FLOATING_WINDOWS_PACKAGE
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.ui.layout.align
import com.ivianuu.essentials.ui.material.HorizontalDivider
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.ModelKeyUi2
import com.ivianuu.essentials.ui.navigation.PlayStoreAppDetailsKey
import com.ivianuu.essentials.ui.state.action
import com.ivianuu.essentials.util.ToastContext
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.first

data class FloatingWindowsPickerKey(val actionTitle: String) : Key<Boolean>

@Provide
val floatingWindowsPickerUi: ModelKeyUi2<FloatingWindowsPickerKey, FloatingWindowsPickerModel> = {
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
        textResId = R.string.es_yes,
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
        textResId = R.string.es_no,
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
      ctx.navigator.push(PlayStoreAppDetailsKey(FLOATING_WINDOWS_PACKAGE))
    }
  },
  openFullScreen = action {
    ctx.navigator.pop(ctx.key, false)
  }
)
