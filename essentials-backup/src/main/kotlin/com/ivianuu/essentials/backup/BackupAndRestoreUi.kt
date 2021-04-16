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
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.store.StateBuilder
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.ui.core.localVerticalInsetsPadding
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.util.StringResourceProvider
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Given

class BackupAndRestoreKey : Key<Nothing>

@Given
val backupAndRestoreUi: ModelKeyUi<BackupAndRestoreKey, BackupAndRestoreModel> = {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.es_backup_title)) }) }
    ) {
        LazyColumn(contentPadding = localVerticalInsetsPadding()) {
            item {
                ListItem(
                    title = { Text(stringResource(R.string.es_pref_backup)) },
                    subtitle = { Text(stringResource(R.string.es_pref_backup_summary)) },
                    onClick = { model.backupData() }
                )
            }
            item {
                ListItem(
                    title = { Text(stringResource(R.string.es_pref_restore)) },
                    subtitle = { Text(stringResource(R.string.es_pref_restore_summary)) },
                    onClick = { model.restoreData() }
                )
            }
        }
    }
}

@Optics
data class BackupAndRestoreModel(
    val backupData: () -> Unit = {},
    val restoreData: () -> Unit = {}
)

@Given
fun backupAndRestoreModel(
    @Given createBackupUseCase: CreateBackupUseCase,
    @Given restoreBackupUseCase: RestoreBackupUseCase,
    @Given stringResource: StringResourceProvider,
    @Given toaster: Toaster,
): StateBuilder<KeyUiGivenScope, BackupAndRestoreModel> = {
    action(BackupAndRestoreModel.backupData()) {
        createBackupUseCase()
            .onFailure {
                it.printStackTrace()
                toaster(stringResource(R.string.es_backup_error, emptyList()))
            }
    }
    action(BackupAndRestoreModel.restoreData()) {
        restoreBackupUseCase()
            .onFailure {
                it.printStackTrace()
                toaster(stringResource(R.string.es_restore_error, emptyList()))
            }
    }
}
