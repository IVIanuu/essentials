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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ivianuu.essentials.apps.AppInfo
import com.ivianuu.essentials.apps.getAppInfo
import com.ivianuu.essentials.apps.ui.IntentAppFilter
import com.ivianuu.essentials.apps.ui.apppicker.AppPickerKey
import com.ivianuu.essentials.datastore.android.PrefBinding
import com.ivianuu.essentials.datastore.android.updatePref
import com.ivianuu.essentials.gestures.R
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.gestures.action.ActionIcon
import com.ivianuu.essentials.gestures.action.actions.MediaActionSettingsAction.*
import com.ivianuu.essentials.store.Actions
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.common.InsettingScrollableColumn
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.KeyUiBinding
import com.ivianuu.essentials.ui.navigation.pushKeyForResult
import com.ivianuu.essentials.ui.resource.Idle
import com.ivianuu.essentials.ui.resource.Resource
import com.ivianuu.essentials.ui.resource.reduceResource
import com.ivianuu.essentials.ui.store.UiState
import com.ivianuu.essentials.ui.store.UiStateBinding
import com.ivianuu.essentials.util.stringResource
import com.ivianuu.injekt.FunApi
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.android.ApplicationContext
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach

@FunBinding
fun mediaAction(
    stringResource: stringResource,
    @FunApi key: String,
    @FunApi titleRes: Int,
    @FunApi icon: Flow<ActionIcon>,
) = Action(
    key = key,
    title = stringResource(titleRes),
    icon = icon
)

@FunBinding
suspend fun doMediaAction(
    applicationContext: ApplicationContext,
    mediaIntent: mediaIntent,
    @FunApi keycode: Int
) {
    applicationContext.sendOrderedBroadcast(mediaIntent(KeyEvent.ACTION_DOWN, keycode), null)
    applicationContext.sendOrderedBroadcast(mediaIntent(KeyEvent.ACTION_UP, keycode), null)
}

@FunBinding
suspend fun mediaIntent(
    mediaActionPrefs: Flow<MediaActionPrefs>,
    @FunApi keyEvent: Int,
    @FunApi keycode: Int,
): Intent = Intent(Intent.ACTION_MEDIA_BUTTON).apply {
    putExtra(
        Intent.EXTRA_KEY_EVENT,
        KeyEvent(keyEvent, keycode)
    )

    val mediaApp = mediaActionPrefs.first().mediaApp
    if (mediaApp != null) {
        `package` = mediaApp
    }
}

@PrefBinding("media_action_prefs")
@JsonClass(generateAdapter = true)
data class MediaActionPrefs(
    @Json(name = "media_app") val mediaApp: String? = null,
)

class MediaActionSettingsKey

@KeyUiBinding<MediaActionSettingsKey>
@FunBinding
@Composable
fun MediaActionSettingsScreen(
    state: @UiState MediaActionSettingsState,
    dispatch: DispatchAction<MediaActionSettingsAction>,
) {
    Scaffold(topBar = { TopAppBar(title = { Text(R.string.es_media_app_settings_ui_title) }) }) {
        InsettingScrollableColumn {
            ListItem(
                title = { Text(R.string.es_pref_media_app) },
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

data class MediaActionSettingsState(val mediaApp: Resource<AppInfo> = Idle)

sealed class MediaActionSettingsAction {
    object UpdateMediaApp : MediaActionSettingsAction()
}

@UiStateBinding
fun mediaActionSettingsState(
    scope: CoroutineScope,
    initial: @Initial MediaActionSettingsState = MediaActionSettingsState(),
    actions: Actions<MediaActionSettingsAction>,
    getAppInfo: getAppInfo,
    intentAppFilterFactory: (Intent) -> IntentAppFilter,
    prefs: Flow<MediaActionPrefs>,
    pickMediaApp: pushKeyForResult<AppPickerKey, AppInfo>,
    updatePrefs: updatePref<MediaActionPrefs>,
) = scope.state(initial) {
    prefs
        .map { it.mediaApp }
        .mapNotNull { if (it != null) getAppInfo(it) else null }
        .reduceResource(this) { copy(mediaApp = it) }
        .launchIn(this)

    actions
        .filterIsInstance<UpdateMediaApp>()
        .onEach {
            @Suppress("DEPRECATION") val newMediaApp = pickMediaApp(
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
