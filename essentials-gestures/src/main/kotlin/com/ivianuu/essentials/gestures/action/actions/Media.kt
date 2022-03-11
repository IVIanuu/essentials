/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.gestures.action.actions

import android.content.*
import android.provider.*
import android.view.*
import androidx.compose.foundation.*
import androidx.compose.material.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.android.prefs.*
import com.ivianuu.essentials.apps.*
import com.ivianuu.essentials.apps.ui.*
import com.ivianuu.essentials.apps.ui.apppicker.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.resource.*
import com.ivianuu.essentials.state.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*

fun interface MediaActionSender : suspend (Int) -> Unit

@Provide fun mediaActionSender(
  context: AppContext,
  prefs: DataStore<MediaActionPrefs>
) = MediaActionSender { keycode ->
  val currentPrefs = prefs.data.first()
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
    @Provide val prefModule = PrefModule { MediaActionPrefs() }
  }
}

class MediaActionSettingsKey : Key<Unit>

@Provide
val mediaActionSettingsUi = ModelKeyUi<MediaActionSettingsKey, MediaActionSettingsModel> {
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
  intentAppPredicateFactory: (Intent) -> IntentAppPredicate,
  pref: DataStore<MediaActionPrefs>,
  ctx: KeyUiContext<MediaActionSettingsKey>
) = Model {
  MediaActionSettingsModel(
    mediaApp = pref.data
      .map { it.mediaApp }
      .flatMapLatest { if (it != null) appRepository.appInfo(it) else infiniteEmptyFlow() }
      .bindResource(),
    updateMediaApp = action {
      val newMediaApp = ctx.navigator.push(
        AppPickerKey(
          intentAppPredicateFactory(Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER)), null
        )
      )
      if (newMediaApp != null)
        pref.updateData { copy(mediaApp = newMediaApp.packageName) }
    }
  )
}
