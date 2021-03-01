/*
 * Copyright 2020 Manuel Wrage
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.AppRepository
import com.ivianuu.essentials.apps.ui.IntentAppFilter
import com.ivianuu.essentials.apps.ui.apppicker.AppPickerKey
import com.ivianuu.essentials.datastore.android.PrefModule
import com.ivianuu.essentials.datastore.android.PrefUpdater
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.actions.MediaActionSettingsAction.UpdateMediaApp
import com.ivianuu.essentials.store.Actions
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.core.localVerticalInsets
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.pushForResult
import com.ivianuu.essentials.ui.resource.Idle
import com.ivianuu.essentials.ui.resource.Resource
import com.ivianuu.essentials.ui.resource.reduceResource
import com.ivianuu.essentials.ui.store.UiState
import com.ivianuu.essentials.ui.store.UiStateBinding
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.android.AppContext
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach

typealias MediaActionSender = suspend (Int) -> Unit

@Given
fun mediaActionSender(
    @Given appContext: AppContext,
    @Given prefs: Flow<MediaActionPrefs>
): MediaActionSender = { keycode ->
    val currentPrefs = prefs.first()
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

@JsonClass(generateAdapter = true)
data class MediaActionPrefs(
    @Json(name = "media_app") val mediaApp: String? = null,
)

@Module
val mediaActionPrefsModule = PrefModule<MediaActionPrefs>("media_action_prefs")

class MediaActionSettingsKey : Key<Nothing>

@Module
val mediaActionSettingsKeyModule = KeyModule<MediaActionSettingsKey>()

@Given
fun mediaActionSettingsUi(
    @Given stateProvider: @Composable () -> @UiState MediaActionSettingsState,
    @Given dispatch: DispatchAction<MediaActionSettingsAction>,
): KeyUi<MediaActionSettingsKey> = {
    val state = stateProvider()
    Scaffold(topBar = { TopAppBar(title = { Text(stringResource(R.string.es_media_app_settings_ui_title)) }) }) {
        LazyColumn(contentPadding = localVerticalInsets()) {
            item {
                ListItem(
                    title = { Text(stringResource(R.string.es_pref_media_app)) },
                    subtitle = {
                        Text(
                            stringResource(
                                R.string.es_pref_media_app_summary,
                                state.mediaApp()?.appName ?: stringResource(R.string.es_none)
                            )
                        )
                    },
                    onClick = { dispatch(UpdateMediaApp) }
                )
            }
        }
    }
}

data class MediaActionSettingsState(val mediaApp: Resource<AppInfo> = Idle)

sealed class MediaActionSettingsAction {
    object UpdateMediaApp : MediaActionSettingsAction()
}

@UiStateBinding
@Given
fun mediaActionSettingsState(
    @Given scope: CoroutineScope,
    @Given initial: @Initial MediaActionSettingsState = MediaActionSettingsState(),
    @Given actions: Actions<MediaActionSettingsAction>,
    @Given appRepository: AppRepository,
    @Given intentAppFilterFactory: (@Given Intent) -> IntentAppFilter,
    @Given navigator: DispatchAction<NavigationAction>,
    @Given prefs: Flow<MediaActionPrefs>,
    @Given updatePrefs: PrefUpdater<MediaActionPrefs>,
): StateFlow<MediaActionSettingsState> = scope.state(initial) {
    prefs
        .map { it.mediaApp }
        .mapNotNull { if (it != null) appRepository.getAppInfo(it) else null }
        .reduceResource(this) { copy(mediaApp = it) }
        .launchIn(this)

    actions
        .filterIsInstance<UpdateMediaApp>()
        .onEach {
            @Suppress("DEPRECATION")
            val newMediaApp = navigator.pushForResult(
                AppPickerKey(
                    intentAppFilterFactory(Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER)), null
                )
            )
            if (newMediaApp != null) {
                updatePrefs { copy(mediaApp = newMediaApp.packageName) }
            }
        }
        .launchIn(this)
}
