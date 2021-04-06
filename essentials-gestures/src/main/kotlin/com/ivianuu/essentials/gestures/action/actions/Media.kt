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
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.android.prefs.PrefStoreModule
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.AppRepository
import com.ivianuu.essentials.apps.ui.IntentAppFilter
import com.ivianuu.essentials.apps.ui.apppicker.AppPickerKey
import com.ivianuu.essentials.data.ValueAction
import com.ivianuu.essentials.data.updateAndAwait
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.actions.MediaActionSettingsAction.UpdateMediaApp
import com.ivianuu.essentials.resource.Idle
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.flowAsResource
import com.ivianuu.essentials.resource.get
import com.ivianuu.essentials.store.Sink
import com.ivianuu.essentials.store.Store
import com.ivianuu.essentials.store.StoreBuilder
import com.ivianuu.essentials.store.onAction
import com.ivianuu.essentials.ui.core.localVerticalInsetsPadding
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.NavigationAction
import com.ivianuu.essentials.ui.navigation.StoreKeyUi
import com.ivianuu.essentials.ui.navigation.pushAndAwait
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.AppContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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

@Serializable
data class MediaActionPrefs(
    @SerialName("media_app") val mediaApp: String? = null,
)

@Given
val mediaActionPrefsModule = PrefStoreModule<MediaActionPrefs>("media_action_prefs")

class MediaActionSettingsKey : Key<Nothing>

@Given
val mediaActionSettingsUi: StoreKeyUi<MediaActionSettingsKey, MediaActionSettingsState,
        MediaActionSettingsAction> = {
    Scaffold(topBar = {
        TopAppBar(title = { Text(stringResource(R.string.es_media_app_settings_ui_title)) })
    }) {
        LazyColumn(contentPadding = localVerticalInsetsPadding()) {
            item {
                ListItem(
                    title = { Text(stringResource(R.string.es_pref_media_app)) },
                    subtitle = {
                        Text(
                            stringResource(
                                R.string.es_pref_media_app_summary,
                                state.mediaApp.get()?.appName
                                    ?: stringResource(R.string.es_none)
                            )
                        )
                    },
                    onClick = { send(UpdateMediaApp) }
                )
            }
        }
    }
}

data class MediaActionSettingsState(val mediaApp: Resource<AppInfo> = Idle)

sealed class MediaActionSettingsAction {
    object UpdateMediaApp : MediaActionSettingsAction()
}

@Given
fun mediaActionSettingsStore(
    @Given appRepository: AppRepository,
    @Given intentAppFilterFactory: (@Given Intent) -> IntentAppFilter,
    @Given navigator: Sink<NavigationAction>,
    @Given pref: Store<MediaActionPrefs, ValueAction<MediaActionPrefs>>,
): StoreBuilder<KeyUiGivenScope, MediaActionSettingsState, MediaActionSettingsAction> = {
    pref
        .map { it.mediaApp }
        .mapNotNull { if (it != null) appRepository.getAppInfo(it) else null }
        .flowAsResource()
        .update { copy(mediaApp = it) }
    onAction<UpdateMediaApp> {
        val newMediaApp = navigator.pushAndAwait(
            AppPickerKey(
                intentAppFilterFactory(Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER)), null
            )
        )
        if (newMediaApp != null) {
            pref.updateAndAwait { copy(mediaApp = newMediaApp.packageName) }
        }
    }
}
