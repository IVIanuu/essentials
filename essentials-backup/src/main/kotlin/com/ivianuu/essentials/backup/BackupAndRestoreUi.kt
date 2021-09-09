/*
 * Copyright 2021 Manuel Wrage
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

import androidx.compose.material.Icon
import androidx.compose.material.Text
import com.github.michaelbull.result.onFailure
import com.ivianuu.essentials.ResourceProvider
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.coroutines.InjektCoroutineScope
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.StateFlow

object BackupAndRestoreKey : Key<Unit>

@Provide val backupAndRestoreUi: ModelKeyUi<BackupAndRestoreKey, BackupAndRestoreModel> = {
  SimpleListScreen(R.string.es_backup_and_restore_title) {
    item {
      ListItem(
        leading = { Icon(R.drawable.es_ic_save) },
        title = { Text(R.string.es_pref_backup) },
        subtitle = { Text(R.string.es_pref_backup_summary) },
        onClick = model.backupData
      )
    }
    item {
      ListItem(
        leading = { Icon(R.drawable.es_ic_restore) },
        title = { Text(R.string.es_pref_restore) },
        subtitle = { Text(R.string.es_pref_restore_summary) },
        onClick = model.restoreData
      )
    }
  }
}

@Optics data class BackupAndRestoreModel(
  val backupData: () -> Unit = {},
  val restoreData: () -> Unit = {}
)

@Provide fun backupAndRestoreModel(
  createBackupUseCase: CreateBackupUseCase,
  restoreBackupUseCase: RestoreBackupUseCase,
  scope: InjektCoroutineScope<KeyUiScope>,
  rp: ResourceProvider,
  toaster: Toaster,
): @Scoped<KeyUiScope> StateFlow<BackupAndRestoreModel> =
  scope.state(BackupAndRestoreModel()) {
    action(BackupAndRestoreModel.backupData()) {
      createBackupUseCase()
        .onFailure {
          it.printStackTrace()
          showToast(R.string.es_backup_error)
        }
    }
    action(BackupAndRestoreModel.restoreData()) {
      restoreBackupUseCase()
        .onFailure {
          it.printStackTrace()
          showToast(R.string.es_restore_error)
        }
    }
  }
