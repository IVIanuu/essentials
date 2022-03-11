/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.backup

import androidx.compose.foundation.*
import androidx.compose.material.*
import androidx.compose.ui.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.state.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*

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
