/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.backup

import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import essentials.*
import essentials.compose.scopedAction
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import essentials.util.*
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
