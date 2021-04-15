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
import androidx.compose.ui.res.stringResource
import com.github.michaelbull.result.onFailure
import com.ivianuu.essentials.backup.BackupAndRestoreAction.BackupData
import com.ivianuu.essentials.backup.BackupAndRestoreAction.RestoreData
import com.ivianuu.essentials.store.StoreBuilder
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.ui.core.localVerticalInsetsPadding
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.StoreKeyUi
import com.ivianuu.essentials.util.StringResourceProvider
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Given

class BackupAndRestoreKey : Key<Nothing>

@Given
val backupAndRestoreUi: StoreKeyUi<BackupAndRestoreKey, BackupAndRestoreState,
        BackupAndRestoreAction> = {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.es_backup_title)) }) }
    ) {
        LazyColumn(contentPadding = localVerticalInsetsPadding()) {
            item {
                ListItem(
                    title = { Text(stringResource(R.string.es_pref_backup)) },
                    subtitle = { Text(stringResource(R.string.es_pref_backup_summary)) },
                    onClick = { send(BackupData) }
                )
            }
            item {
                ListItem(
                    title = { Text(stringResource(R.string.es_pref_restore)) },
                    subtitle = { Text(stringResource(R.string.es_pref_restore_summary)) },
                    onClick = { send(RestoreData) }
                )
            }
        }
    }
}

class BackupAndRestoreState

sealed class BackupAndRestoreAction {
    object BackupData : BackupAndRestoreAction()
    object RestoreData : BackupAndRestoreAction()
}

@Given
fun backupAndRestoreStore(
    @Given createBackupUseCase: CreateBackupUseCase,
    @Given restoreBackupUseCase: RestoreBackupUseCase,
    @Given stringResource: StringResourceProvider,
    @Given toaster: Toaster,
): StoreBuilder<KeyUiGivenScope, BackupAndRestoreState, BackupAndRestoreAction> = {
    action<BackupData> {
        createBackupUseCase()
            .onFailure {
                it.printStackTrace()
                toaster(stringResource(R.string.es_backup_error, emptyList()))
            }
    }
    action<RestoreData> {
        restoreBackupUseCase()
            .onFailure {
                it.printStackTrace()
                toaster(stringResource(R.string.es_restore_error, emptyList()))
            }
    }
}
