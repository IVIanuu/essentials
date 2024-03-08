/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.content.*
import android.provider.*
import android.view.*
import androidx.compose.material.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.apps.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*

@Provide class MediaActionSender(
  private val appContext: AppContext,
  private val prefs: DataStore<MediaActionPrefs>
) {
  suspend fun sendMediaAction(keycode: Int) {
    val currentPrefs = prefs.data.first()
    appContext.sendOrderedBroadcast(mediaIntentFor(KeyEvent.ACTION_DOWN, keycode, currentPrefs), null)
    appContext.sendOrderedBroadcast(mediaIntentFor(KeyEvent.ACTION_UP, keycode, currentPrefs), null)
  }

  private fun mediaIntentFor(
    keyEvent: Int,
    keycode: Int,
    prefs: MediaActionPrefs
  ) = Intent(Intent.ACTION_MEDIA_BUTTON).apply {
    putExtra(
      Intent.EXTRA_KEY_EVENT,
      KeyEvent(keyEvent, keycode)
    )

    val mediaApp = prefs.mediaApp
    if (mediaApp != null) {
      `package` = mediaApp
    }
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
      ScreenScaffold(topBar = { AppBar { Text("Media action settings") } }) {
        VerticalList {
          item {
            ListItem(
              onClick = scopedAction {
                val newMediaApp = navigator.push(
                  AppPickerScreen(
                    intentAppPredicateFactory(Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER)), null
                  )
                )
                if (newMediaApp != null)
                  pref.updateData { copy(mediaApp = newMediaApp.packageName) }
              },
              title = { Text("Media app") },
              subtitle = {
                val mediaAppName = pref.data.scopedState(null)?.mediaApp
                  ?.let { appRepository.appInfo(it).scopedState(null)?.appName }
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
