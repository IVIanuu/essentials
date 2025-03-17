/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.gestures.action.actions

import android.content.*
import android.content.pm.*
import android.provider.*
import android.view.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.datastore.core.*
import androidx.datastore.preferences.core.*
import essentials.*
import essentials.apps.*
import essentials.compose.*
import essentials.gestures.action.*
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import injekt.*
import kotlinx.coroutines.flow.*

@Provide object PlayPauseActionId : MediaActionId(
  "media_play_pause",
  KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
) {
  @Provide val action get() = Action(
    id = PlayPauseActionId,
    title = "Media play/Pause",
    icon = { Icon(Icons.Default.PlayArrow, null) }
  )
}

@Provide object SkipNextActionId : MediaActionId(
  "media_skip_next",
  KeyEvent.KEYCODE_MEDIA_NEXT
) {
  @Provide val action get() = Action(
    id = SkipNextActionId,
    title = "Media skip next",
    icon = { Icon(Icons.Default.SkipNext, null) }
  )
}

@Provide object SkipPreviousActionId : MediaActionId(
  "media_skip_previous",
  KeyEvent.KEYCODE_MEDIA_PREVIOUS
) {
  @Provide val action get() = Action(
    id = SkipPreviousActionId,
    title = "Media skip previous",
    icon = { Icon(Icons.Default.SkipPrevious, null) }
  )
}

@Provide object StopActionId : MediaActionId(
  "media_stop",
  KeyEvent.KEYCODE_MEDIA_STOP
) {
  @Provide val action get() = Action(
    id = StopActionId,
    title = "Media stop",
    icon = { Icon(Icons.Default.Stop, null) }
  )
}

abstract class MediaActionId(
  value: String,
  val keycode: Int
) : ActionId(value) {
  @Provide companion object {
    @Provide suspend fun <@AddOn T : MediaActionId> execute(
      id: T,
      appContext: AppContext,
      preferencesStore: DataStore<Preferences>
    ): ActionExecutorResult<T> {
      val mediaApp = preferencesStore.data.first()[MediaActionAppKey]
      fun mediaIntentFor(keyEvent: Int) = Intent(Intent.ACTION_MEDIA_BUTTON).apply {
        putExtra(Intent.EXTRA_KEY_EVENT, KeyEvent(keyEvent, id.keycode))
        if (mediaApp != null) `package` = mediaApp
      }
      appContext.sendOrderedBroadcast(mediaIntentFor(KeyEvent.ACTION_DOWN), null)
      appContext.sendOrderedBroadcast(mediaIntentFor(KeyEvent.ACTION_UP), null)
    }

    @Provide fun <@AddOn I : MediaActionId> settingsScreen(): ActionSettingsScreen<I> =
      MediaActionSettingsScreen()
  }
}

class MediaActionSettingsScreen : Screen<Unit>

@Provide @Composable fun MediaActionSettingsUi(
  getAppInfo: suspend (String) -> AppInfo?,
  navigator: Navigator,
  packageManager: PackageManager = inject,
  preferencesStore: DataStore<Preferences>,
  scope: Scope<ScreenScope> = inject,
): Ui<MediaActionSettingsScreen> {
  EsScaffold(topBar = { EsAppBar { Text("Media action settings") } }) {
    EsLazyColumn {
      item {
        EsListItem(
          onClick = scopedAction {
            val newMediaApp = navigator.push(
              AppPickerScreen(
                Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER)
                  .asAppPredicate(), null
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
