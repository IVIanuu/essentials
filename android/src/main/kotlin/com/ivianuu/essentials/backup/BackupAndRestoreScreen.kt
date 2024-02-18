/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.backup

import androidx.compose.material.*
import androidx.compose.ui.res.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.android.R
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
    ) = Ui<BackupAndRestoreScreen, Unit> {
      ScreenScaffold(topBar = { AppBar { Text("Backup/Restore") } }) {
        VerticalList {
          item {
            ListItem(
              onClick = action {
                catch { backupManager.createBackup() }
                  .onLeft {
                    it.printStackTrace()
                    toaster(R.string.backup_error)
                  }
              },
              leading = { Icon(painterResource(R.drawable.ic_save), null) },
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
                    toaster("Failed to restore your data!")
                  }
              },
              leading = { Icon(painterResource(R.drawable.ic_restore), null) },
              title = { Text("Restore") },
              subtitle = { Text("Restore your data") }
            )
          }
        }
      }
    }
  }
}
