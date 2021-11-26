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

import androidx.compose.foundation.clickable
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.onFailure
import com.ivianuu.essentials.state.action
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.util.ToastContext
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide

object BackupAndRestoreKey : Key<Unit>

@Provide val backupAndRestoreUi = ModelKeyUi<BackupAndRestoreKey, BackupAndRestoreModel> {
  SimpleListScreen(R.string.es_backup_and_restore_title) {
    item {
      ListItem(
        modifier = Modifier.clickable(onClick = model.backupData),
        leading = { Icon(R.drawable.es_ic_save) },
        title = { Text(R.string.es_pref_backup) },
        subtitle = { Text(R.string.es_pref_backup_summary) }
      )
    }
    item {
      ListItem(
        modifier = Modifier.clickable(onClick = model.restoreData),
        leading = { Icon(R.drawable.es_ic_restore) },
        title = { Text(R.string.es_pref_restore) },
        subtitle = { Text(R.string.es_pref_restore_summary) }
      )
    }
  }
}

data class BackupAndRestoreModel(val backupData: () -> Unit, val restoreData: () -> Unit)

@Provide @Composable fun backupAndRestoreModel(
  createBackup: CreateBackupUseCase,
  restoreBackup: RestoreBackupUseCase,
  T: ToastContext,
  ctx: KeyUiContext<BackupAndRestoreKey>
) = BackupAndRestoreModel(
  backupData = action {
    createBackup()
      .onFailure {
        it.printStackTrace()
        showToast(R.string.es_backup_error)
      }
  },
  restoreData = action {
    restoreBackup()
      .onFailure {
        it.printStackTrace()
        showToast(R.string.es_restore_error)
      }
  }
)
