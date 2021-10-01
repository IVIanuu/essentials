/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.content.Intent
import android.provider.MediaStore
import android.view.KeyEvent
import androidx.compose.material.Text
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.android.prefs.PrefModule
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.AppRepository
import com.ivianuu.essentials.apps.ui.IntentAppPredicate
import com.ivianuu.essentials.apps.ui.apppicker.AppPickerKey
import com.ivianuu.essentials.coroutines.infiniteEmptyFlow
import com.ivianuu.essentials.data.DataStore
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.ActionId
import com.ivianuu.essentials.gestures.action.ActionSettingsKey
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.resource.Idle
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.flowAsResource
import com.ivianuu.essentials.resource.getOrNull
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

typealias MediaActionSender = suspend (Int) -> Unit

@Provide fun mediaActionSender(
  context: AppContext,
  prefs: Flow<MediaActionPrefs>
): MediaActionSender = { keycode ->
  val currentPrefs = prefs.first()
  context.sendOrderedBroadcast(mediaIntentFor(KeyEvent.ACTION_DOWN, keycode, currentPrefs), null)
  context.sendOrderedBroadcast(mediaIntentFor(KeyEvent.ACTION_UP, keycode, currentPrefs), null)
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

@Serializable data class MediaActionPrefs(
  @SerialName("media_app") val mediaApp: String? = null,
) {
  companion object {
    @Provide val prefModule = PrefModule("media_action_prefs") { MediaActionPrefs() }
  }
}

class MediaActionSettingsKey<I : ActionId> : ActionSettingsKey<I>

@Provide
val mediaActionSettingsUi: ModelKeyUi<MediaActionSettingsKey<*>, MediaActionSettingsModel> = {
  SimpleListScreen(R.string.es_media_app_settings_ui_title) {
    item {
      ListItem(
        title = { Text(R.string.es_pref_media_app) },
        subtitle = {
          Text(
            stringResource(
              R.string.es_pref_media_app_summary,
              model.mediaApp.getOrNull()?.appName ?: stringResource(R.string.es_none)
            )
          )
        },
        onClick = model.updateMediaApp
      )
    }
  }
}

@Optics data class MediaActionSettingsModel(
  val mediaApp: Resource<AppInfo?> = Idle,
  val updateMediaApp: () -> Unit = {}
)

@Provide fun mediaActionSettingsModel(
  appRepository: AppRepository,
  intentAppPredicateFactory: (@Provide Intent) -> IntentAppPredicate,
  navigator: Navigator,
  pref: DataStore<MediaActionPrefs>,
  scope: NamedCoroutineScope<KeyUiScope>
): StateFlow<MediaActionSettingsModel> = scope.state(
  MediaActionSettingsModel()
) {
  pref.data
    .map { it.mediaApp }
    .flatMapLatest { if (it != null) appRepository.appInfo(it) else infiniteEmptyFlow() }
    .flowAsResource()
    .update { copy(mediaApp = it) }

  action(MediaActionSettingsModel.updateMediaApp()) {
    val newMediaApp = navigator.push(
      AppPickerKey(
        intentAppPredicateFactory(Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER)), null
      )
    )
    if (newMediaApp != null) {
      pref.updateData { copy(mediaApp = newMediaApp.packageName) }
    }
  }
}
