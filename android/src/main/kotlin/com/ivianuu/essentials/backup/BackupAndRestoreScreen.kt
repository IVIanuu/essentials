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
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.navigation.Presenter
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Provide

class BackupAndRestoreScreen : Screen<Unit>

@Provide val backupAndRestoreUi = Ui<BackupAndRestoreScreen, BackupAndRestoreState> { state ->
  Scaffold(topBar = { AppBar { Text(stringResource(R.string.es_backup_and_restore_title)) } }) {
    VerticalList {
      item {
        ListItem(
          modifier = Modifier.clickable(onClick = state.backupData),
          leading = { Icon(painterResource(R.drawable.es_ic_save), null) },
          title = { Text(stringResource(R.string.es_pref_backup)) },
          subtitle = { Text(stringResource(R.string.es_pref_backup_summary)) }
        )
      }
      item {
        ListItem(
          modifier = Modifier.clickable(onClick = state.restoreData),
          leading = { Icon(painterResource(R.drawable.es_ic_restore), null) },
          title = { Text(stringResource(R.string.es_pref_restore)) },
          subtitle = { Text(stringResource(R.string.es_pref_restore_summary)) }
        )
      }
    }
  }
}

data class BackupAndRestoreState(val backupData: () -> Unit, val restoreData: () -> Unit)

@Provide fun backupAndRestorePresenter(
  backupManager: BackupManager,
  toaster: Toaster,
) = Presenter {
  BackupAndRestoreState(
    backupData = action {
      Either.catch { backupManager.createBackup() }
        .onLeft {
          it.printStackTrace()
          toaster(R.string.es_backup_error)
        }
    },
    restoreData = action {
      Either.catch { backupManager.restoreBackup() }
        .onLeft {
          it.printStackTrace()
          toaster(R.string.es_restore_error)
        }
    }
  )
}
