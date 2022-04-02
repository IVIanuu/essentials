/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.backup

import androidx.compose.foundation.clickable
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import com.github.michaelbull.result.onFailure
import com.ivianuu.essentials.state.action
import com.ivianuu.essentials.ui.common.SimpleListScreen
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiContext
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.util.ToastContext
import com.ivianuu.essentials.util.showToast
import com.ivianuu.injekt.Provide

object BackupAndRestoreKey : Key<Unit>

@Provide val backupAndRestoreUi = ModelKeyUi<BackupAndRestoreKey, BackupAndRestoreModel> {
  SimpleListScreen(R.string.es_backup_and_restore_title) {
    item {
      ListItem(
        modifier = Modifier.clickable(onClick = backupData),
        leading = { Icon(R.drawable.es_ic_save) },
        title = { Text(R.string.es_pref_backup) },
        subtitle = { Text(R.string.es_pref_backup_summary) }
      )
    }
    item {
      ListItem(
        modifier = Modifier.clickable(onClick = restoreData),
        leading = { Icon(R.drawable.es_ic_restore) },
        title = { Text(R.string.es_pref_restore) },
        subtitle = { Text(R.string.es_pref_restore_summary) }
      )
    }
  }
}

data class BackupAndRestoreModel(val backupData: () -> Unit, val restoreData: () -> Unit)

@Provide fun backupAndRestoreModel(
  createBackup: CreateBackupUseCase,
  restoreBackup: RestoreBackupUseCase,
  T: ToastContext,
  ctx: KeyUiContext<BackupAndRestoreKey>
) = Model {
  BackupAndRestoreModel(
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
}
