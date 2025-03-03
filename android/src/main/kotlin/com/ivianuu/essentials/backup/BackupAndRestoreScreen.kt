/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.backup

import androidx.compose.material3.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Modifier
import com.ivianuu.essentials.*
import com.ivianuu.essentials.compose.scopedAction
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import injekt.*

class BackupAndRestoreScreen : Screen<Unit> {
  @Provide companion object {
    @Provide fun ui(
      backupManager: BackupManager,
      toaster: Toaster,
    ) = Ui<BackupAndRestoreScreen> {
      EsScaffold(topBar = { EsAppBar { Text("Backup/Restore") } }) {
        EsLazyColumn {
          item {
            EsListItem(
              onClick = scopedAction {
                catch { backupManager.createBackup() }
                  .onLeft {
                    it.printStackTrace()
                    toaster.toast("Failed to backup your data!")
                  }
              },
              leadingContent = { Icon(Icons.Default.Save, null) },
              headlineContent = { Text("Backup") },
              supportingContent = { Text("Export your data") }
            )
          }
          item {
            EsListItem(
              onClick = scopedAction {
                catch { backupManager.restoreBackup() }
                  .onLeft {
                    it.printStackTrace()
                    toaster.toast("Failed to restore your data!")
                  }
              },
              leadingContent = { Icon(Icons.Default.Restore, null) },
              headlineContent = { Text("Restore") },
              supportingContent = { Text("Restore your data") }
            )
          }
        }
      }
    }
  }
}
