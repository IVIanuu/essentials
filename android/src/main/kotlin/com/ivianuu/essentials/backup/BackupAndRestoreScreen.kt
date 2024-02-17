/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.backup

import androidx.compose.foundation.clickable
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import arrow.core.Either
import com.ivianuu.essentials.android.R
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.ui.common.VerticalList
import com.ivianuu.essentials.ui.material.AppBar
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.ScreenScaffold
import com.ivianuu.essentials.ui.navigation.Presenter
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Provide

class BackupAndRestoreScreen : Screen<Unit> {
  @Provide companion object {
    @Provide fun ui(
      backupManager: BackupManager,
      toaster: Toaster,
    ) = Ui<BackupAndRestoreScreen, Unit> {
      ScreenScaffold(topBar = { AppBar { Text(stringResource(R.string.backup_and_restore_title)) } }) {
        VerticalList {
          item {
            ListItem(
              modifier = Modifier.clickable(onClick = action {
                Either.catch { backupManager.createBackup() }
                  .onLeft {
                    it.printStackTrace()
                    toaster(R.string.backup_error)
                  }
              }),
              leading = { Icon(painterResource(R.drawable.ic_save), null) },
              title = { Text(stringResource(R.string.pref_backup)) },
              subtitle = { Text(stringResource(R.string.pref_backup_summary)) }
            )
          }
          item {
            ListItem(
              modifier = Modifier.clickable(onClick = action {
                Either.catch { backupManager.restoreBackup() }
                  .onLeft {
                    it.printStackTrace()
                    toaster(R.string.restore_error)
                  }
              }),
              leading = { Icon(painterResource(R.drawable.ic_restore), null) },
              title = { Text(stringResource(R.string.pref_restore)) },
              subtitle = { Text(stringResource(R.string.pref_restore_summary)) }
            )
          }
        }
      }
    }
  }
}
