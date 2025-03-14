/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action.actions

import android.content.*
import android.provider.*
import android.view.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.datastore.core.*
import androidx.datastore.preferences.core.*
import essentials.*
import essentials.apps.*
import essentials.compose.*
import essentials.data.preferencesDataStore
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import injekt.*
import kotlinx.coroutines.flow.*

suspend fun sendMediaAction(keycode: Int, scope: Scope<*> = inject) {
  val mediaApp = preferencesDataStore().data.first()[MediaActionAppKey]
  fun mediaIntentFor(keyEvent: Int) = Intent(Intent.ACTION_MEDIA_BUTTON).apply {
    putExtra(Intent.EXTRA_KEY_EVENT, KeyEvent(keyEvent, keycode))
    if (mediaApp != null) `package` = mediaApp
  }
  appContext().sendOrderedBroadcast(mediaIntentFor(KeyEvent.ACTION_DOWN), null)
  appContext().sendOrderedBroadcast(mediaIntentFor(KeyEvent.ACTION_UP), null)
}

class MediaActionSettingsScreen : Screen<Unit>

@Provide @Composable fun MediaActionSettingsUi(
  scope: Scope<*> = inject
): Ui<MediaActionSettingsScreen> {
  EsScaffold(topBar = { EsAppBar { Text("Media action settings") } }) {
    EsLazyColumn {
      item {
        EsListItem(
          onClick = scopedAction {
            val newMediaApp = navigator().push(
              AppPickerScreen(
                intentAppPredicate(Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER))
              )
            )
            if (newMediaApp != null)
              preferencesDataStore().edit { it[MediaActionAppKey] = newMediaApp.packageName }
          },
          headlineContent = { Text("Media app") },
          supportingContent = {
            val mediaAppName by produceScopedState(nullOf()) {
              preferencesDataStore().data
                .map { it[MediaActionAppKey] }
                .mapLatest {
                  if (it == null) null
                  else getAppInfo(it)?.appName
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
