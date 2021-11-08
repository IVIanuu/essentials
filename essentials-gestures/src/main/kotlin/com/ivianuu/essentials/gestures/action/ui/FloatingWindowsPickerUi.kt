package com.ivianuu.essentials.gestures.action.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.apps.AppRepository
import com.ivianuu.essentials.floatingwindows.FLOATING_WINDOWS_PACKAGE
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.layout.align
import com.ivianuu.essentials.ui.material.HorizontalDivider
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiComponent
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.PlayStoreAppDetailsKey
import com.ivianuu.essentials.util.Toasts
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.ComponentScope
import kotlinx.coroutines.flow.first

data class FloatingWindowsPickerKey(val actionTitle: String) : Key<Boolean>

@Provide
val floatingWindowsPickerUi: ModelKeyUi<FloatingWindowsPickerKey, FloatingWindowsPickerModel> = {
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

@Optics data class FloatingWindowsPickerModel(
  val actionTitle: String = "",
  val openFloatingWindow: () -> Unit = {},
  val openFullScreen: () -> Unit = {}
)

@Provide @Toasts fun floatingWindowsPickerModel(
  appRepository: AppRepository,
  key: FloatingWindowsPickerKey,
  navigator: Navigator,
  scope: ComponentScope<KeyUiComponent>
) = scope.state(FloatingWindowsPickerModel(key.actionTitle)) {
  action(FloatingWindowsPickerModel.openFloatingWindow()) {
    if (appRepository.isAppInstalled(FLOATING_WINDOWS_PACKAGE).first()) {
      navigator.pop(key, true)
    } else {
      showToast(R.string.es_floating_windows_not_installed)
      navigator.push(PlayStoreAppDetailsKey(FLOATING_WINDOWS_PACKAGE))
    }
  }

  action(FloatingWindowsPickerModel.openFullScreen()) { navigator.pop(key, false) }
}
