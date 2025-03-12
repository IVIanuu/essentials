/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action.actions

import android.content.*
import android.provider.*
import android.view.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import essentials.*
import essentials.apps.*
import essentials.compose.*
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import injekt.*
import kotlinx.coroutines.flow.*

@Provide class MediaActionSender(
  private val appContext: AppContext,
  private val preferencesStore: DataStore<Preferences>
) {
  suspend fun sendMediaAction(keycode: Int) {
    val mediaApp = preferencesStore.data.first()[MediaActionAppKey]
    fun mediaIntentFor(keyEvent: Int) = Intent(Intent.ACTION_MEDIA_BUTTON).apply {
      putExtra(Intent.EXTRA_KEY_EVENT, KeyEvent(keyEvent, keycode))
      if (mediaApp != null) `package` = mediaApp
    }
    appContext.sendOrderedBroadcast(mediaIntentFor(KeyEvent.ACTION_DOWN), null)
    appContext.sendOrderedBroadcast(mediaIntentFor(KeyEvent.ACTION_UP), null)
  }
}

class MediaActionSettingsScreen : Screen<Unit>

@Provide @Composable fun MediaActionSettingsUi(
  appRepository: AppRepository,
  navigator: Navigator,
  intentAppPredicateFactory: (Intent) -> IntentAppPredicate,
  preferencesStore: DataStore<Preferences>
): Ui<MediaActionSettingsScreen> {
  EsScaffold(topBar = { EsAppBar { Text("Media action settings") } }) {
    EsLazyColumn {
      item {
        EsListItem(
          onClick = scopedAction {
            val newMediaApp = navigator.push(
              AppPickerScreen(
                intentAppPredicateFactory(Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER)), null
              )
            )
            if (newMediaApp != null)
              preferencesStore.edit { it[MediaActionAppKey] = newMediaApp.packageName }
          },
          headlineContent = { Text("Media app") },
          supportingContent = {
            val mediaAppName by produceScopedState(nullOf()) {
              preferencesStore.data
                .map { it[MediaActionAppKey] }
                .flatMapLatest {
                  if (it == null) flowOf(null)
                  else appRepository.appInfo(it).map { it?.appName }
                }
                .collect { value = it }
            }

            Text(
              "Define the target app for the media actions (current: ${mediaAppName ?: "None"})"
            )
          }
        )
      }
    }
  }
}

private val MediaActionAppKey = stringPreferencesKey("media_action_app")
