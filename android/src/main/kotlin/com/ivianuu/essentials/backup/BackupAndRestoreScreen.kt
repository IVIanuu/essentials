/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.backup

import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.ui.common.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*

class BackupAndRestoreScreen : Screen<Unit> {
  @Provide companion object {
    @Provide fun ui(
      backupManager: BackupManager,
      toaster: Toaster,
    ) = Ui<BackupAndRestoreScreen> {
      ScreenScaffold(topBar = { AppBar { Text("Backup/Restore") } }) {
        VerticalList {
          item {
            ListItem(
              onClick = action {
                catch { backupManager.createBackup() }
                  .onLeft {
                    it.printStackTrace()
                    toaster.toast("Failed to backup your data!")
                  }
              },
              leading = { Icon(Icons.Default.Save, null) },
              title = { Text("Backup") },
              subtitle = { Text("Export your data") }
            )
          }
          item {
            ListItem(
              onClick = action {
                catch { backupManager.restoreBackup() }
                  .onLeft {
                    it.printStackTrace()
                    toaster.toast("Failed to restore your data!")
                  }
              },
              leading = { Icon(Icons.Default.Restore, null) },
              title = { Text("Restore") },
              subtitle = { Text("Restore your data") }
            )
          }
        }
      }
    }
  }
}
