/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.provider.MediaStore
import android.view.KeyEvent
import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.Resource
import com.ivianuu.essentials.android.prefs.PrefModule
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.AppPickerScreen
import com.ivianuu.essentials.apps.AppRepository
import com.ivianuu.essentials.apps.IntentAppPredicate
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.coroutines.infiniteEmptyFlow
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.getOrNull
import com.ivianuu.essentials.produceResourceState
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.navigation.push
import com.ivianuu.injekt.Provide
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable

fun interface MediaActionSender {
  suspend operator fun invoke(keycode: Int)
}

@Provide fun mediaActionSender(
  appContext: AppContext,
  prefs: DataStore<MediaActionPrefs>
) = MediaActionSender { keycode ->
  val currentPrefs = prefs.data.first()
  appContext.sendOrderedBroadcast(mediaIntentFor(KeyEvent.ACTION_DOWN, keycode, currentPrefs), null)
  appContext.sendOrderedBroadcast(mediaIntentFor(KeyEvent.ACTION_UP, keycode, currentPrefs), null)
}

private fun mediaIntentFor(
  keyEvent: Int,
  keycode: Int,
  prefs: MediaActionPrefs
): Intent = Intent(Intent.ACTION_MEDIA_BUTTON).apply {
  putExtra(
    Intent.EXTRA_KEY_EVENT,
    KeyEvent(keyEvent, keycode)
  )

  val mediaApp = prefs.mediaApp
  if (mediaApp != null) {
    `package` = mediaApp
  }
}

@Serializable data class MediaActionPrefs(val mediaApp: String? = null) {
  companion object {
    @Provide val prefModule = PrefModule { MediaActionPrefs() }
  }
}

class MediaActionSettingsScreen : Screen<Unit>

@Provide
val mediaActionSettingsUi = Ui<MediaActionSettingsScreen, MediaActionSettingsModel> { model ->
  SimpleListScreen(R.string.es_media_app_settings_ui_title) {
    item {
      ListItem(
        modifier = Modifier.clickable(onClick = model.updateMediaApp),
        title = { Text(R.string.es_pref_media_app) },
        subtitle = {
          Text(
            stringResource(
              R.string.es_pref_media_app_summary,
              model.mediaApp.getOrNull()?.appName ?: stringResource(R.string.es_none)
            )
          )
        }
      )
    }
  }
}

data class MediaActionSettingsModel(
  val mediaApp: Resource<AppInfo?>,
  val updateMediaApp: () -> Unit
)

@Provide fun mediaActionSettingsModel(
  appRepository: AppRepository,
  navigator: Navigator,
  intentAppPredicateFactory: (Intent) -> IntentAppPredicate,
  pref: DataStore<MediaActionPrefs>
) = Model {
  MediaActionSettingsModel(
    mediaApp = produceResourceState {
      pref.data
        .map { it.mediaApp }
        .flatMapLatest { if (it != null) appRepository.appInfo(it) else infiniteEmptyFlow() }
        .let { emitAll(it) }
    }.value,
    updateMediaApp = action {
      val newMediaApp = navigator.push(
        AppPickerScreen(
          intentAppPredicateFactory(Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER)), null
        )
      )
      if (newMediaApp != null)
        pref.updateData { copy(mediaApp = newMediaApp.packageName) }
    }
  )
}
