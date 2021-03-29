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

package com.ivianuu.essentials.backup

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.github.michaelbull.result.onFailure
import com.ivianuu.essentials.backup.BackupAndRestoreAction.BackupData
import com.ivianuu.essentials.backup.BackupAndRestoreAction.RestoreData
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.core.localVerticalInsetsPadding
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.util.ScopeCoroutineScope
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class BackupAndRestoreKey : Key<Nothing>

@Given
val backupAndRestoreKeyModule = KeyModule<BackupAndRestoreKey>()

@Given
fun backupAndRestoreUi(
    @Given stateFlow: StateFlow<BackupAndRestoreState>,
    @Given dispatch: Collector<BackupAndRestoreAction>,
): KeyUi<BackupAndRestoreKey> = {
    val state by stateFlow.collectAsState()
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.es_backup_title)) }) }
    ) {
        LazyColumn(contentPadding = localVerticalInsetsPadding()) {
            item {
                ListItem(
                    title = { Text(stringResource(R.string.es_pref_backup)) },
                    subtitle = { Text(stringResource(R.string.es_pref_backup_summary)) },
                    onClick = { dispatch(BackupData) }
                )
            }
            item {
                ListItem(
                    title = { Text(stringResource(R.string.es_pref_restore)) },
                    subtitle = { Text(stringResource(R.string.es_pref_restore_summary)) },
                    onClick = { dispatch(RestoreData) }
                )
            }
        }
    }
}

object BackupAndRestoreState

sealed class BackupAndRestoreAction {
    object BackupData : BackupAndRestoreAction()
    object RestoreData : BackupAndRestoreAction()
}

@Given
fun backupAndRestoreState(
    @Given scope: ScopeCoroutineScope<KeyUiGivenScope>,
    @Given initial: @Initial BackupAndRestoreState = BackupAndRestoreState,
    @Given actions: Flow<BackupAndRestoreAction>,
    @Given backupCreator: BackupCreator,
    @Given backupApplier: BackupApplier,
    @Given toaster: Toaster
): @Scoped<KeyUiGivenScope> StateFlow<BackupAndRestoreState> = scope.state(initial) {
    actions
        .filterIsInstance<BackupData>()
        .onEach {
            backupCreator()
                .onFailure {
                    it.printStackTrace()
                    toaster.showToast(R.string.es_backup_error)
                }
        }
        .launchIn(this)
    actions
        .filterIsInstance<RestoreData>()
        .onEach {
            backupApplier()
                .onFailure {
                    it.printStackTrace()
                    toaster.showToast(R.string.es_restore_error)
                }
        }
        .launchIn(this)
}

@Given
val backupAndRestoreActions: @Scoped<KeyUiGivenScope> MutableSharedFlow<BackupAndRestoreAction>
    get() = EventFlow()
