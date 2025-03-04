/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action.actions

import android.content.*
import android.provider.*
import android.view.*
import androidx.compose.material3.*
import essentials.*
import essentials.apps.*
import essentials.compose.*
import essentials.data.*
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import injekt.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*

@Provide class MediaActionSender(
  private val appContext: AppContext,
  private val pref: DataStore<MediaActionPrefs>
) {
  suspend fun sendMediaAction(keycode: Int) {
    val currentPrefs = pref.data.first()
    fun mediaIntentFor(keyEvent: Int) = Intent(Intent.ACTION_MEDIA_BUTTON).apply {
      putExtra(
        Intent.EXTRA_KEY_EVENT,
        KeyEvent(keyEvent, keycode)
      )

      val mediaApp = currentPrefs.mediaApp
      if (mediaApp != null) {
        `package` = mediaApp
      }
    }
    appContext.sendOrderedBroadcast(mediaIntentFor(KeyEvent.ACTION_DOWN), null)
    appContext.sendOrderedBroadcast(mediaIntentFor(KeyEvent.ACTION_UP), null)
  }
}

@Serializable data class MediaActionPrefs(val mediaApp: String? = null) {
  @Provide companion object {
    @Provide val dataStoreModule = DataStoreModule("media_action_prefs") { MediaActionPrefs() }
  }
}

class MediaActionSettingsScreen : Screen<Unit> {
  @Provide companion object {
    @Provide fun ui(
      appRepository: AppRepository,
      navigator: Navigator,
      intentAppPredicateFactory: (Intent) -> IntentAppPredicate,
      pref: DataStore<MediaActionPrefs>
    ) = Ui<MediaActionSettingsScreen> {
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
                  pref.updateData { copy(mediaApp = newMediaApp.packageName) }
              },
              headlineContent = { Text("Media app") },
              supportingContent = {
                val mediaAppName = pref.data.collectAsScopedState(null).value?.mediaApp
                  ?.let { appRepository.appInfo(it).collectAsScopedState(null).value?.appName }
                Text(
                  "Define the target app for the media actions (current: ${mediaAppName ?: "None"})"
                )
              }
            )
          }
        }
      }
    }
  }
}
