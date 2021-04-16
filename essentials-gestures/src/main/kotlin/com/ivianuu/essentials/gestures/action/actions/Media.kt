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

import android.content.*
import android.provider.*
import android.view.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Text
import androidx.compose.ui.res.*
import com.ivianuu.essentials.android.prefs.*
import com.ivianuu.essentials.apps.*
import com.ivianuu.essentials.apps.ui.*
import com.ivianuu.essentials.apps.ui.apppicker.*
import com.ivianuu.essentials.data.*
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.*
import com.ivianuu.essentials.optics.*
import com.ivianuu.essentials.resource.*
import com.ivianuu.essentials.store.*
import com.ivianuu.essentials.ui.core.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.*

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
val mediaActionPrefsModule = PrefModule<MediaActionPrefs>("media_action_prefs")

class MediaActionSettingsKey<I : ActionId> : ActionSettingsKey<I>

@Given
val mediaActionSettingsUi: ModelKeyUi<MediaActionSettingsKey<*>, MediaActionSettingsModel> = {
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
                                model.mediaApp.get()?.appName
                                    ?: stringResource(R.string.es_none)
                            )
                        )
                    },
                    onClick = model.updateMediaApp
                )
            }
        }
    }
}

@Optics
data class MediaActionSettingsModel(
    val mediaApp: Resource<AppInfo> = Idle,
    val updateMediaApp: () -> Unit = {}
)

@Given
fun mediaActionSettingsModel(
    @Given getAppInfo: GetAppInfoUseCase,
    @Given intentAppPredicateFactory: (@Given Intent) -> IntentAppPredicate,
    @Given navigator: Navigator,
    @Given pref: DataStore<MediaActionPrefs>,
): StateBuilder<KeyUiGivenScope, MediaActionSettingsModel> = {
    pref.data
        .map { it.mediaApp }
        .mapNotNull { if (it != null) getAppInfo(it) else null }
        .flowAsResource()
        .update(MediaActionSettingsModel.mediaApp())
    action(MediaActionSettingsModel.updateMediaApp()) {
        val newMediaApp = navigator.pushForResult(
            AppPickerKey(
                intentAppPredicateFactory(Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER)), null
            )
        )
        if (newMediaApp != null) {
            pref.updateData { copy(mediaApp = newMediaApp.packageName) }
        }
    }
}
