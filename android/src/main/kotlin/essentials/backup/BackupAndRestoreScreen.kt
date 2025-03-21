/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.backup

import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.github.michaelbull.result.*
import essentials.*
import essentials.compose.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import essentials.util.*
import injekt.*

class BackupAndRestoreScreen : OverlayScreen<Unit>

@Provide @Composable fun BackupAndRestoreUi(
  createBackup: createBackup,
  restoreBackup: restoreBackup,
  context: ScreenContext<BackupAndRestoreScreen> = inject,
  showToast: showToast,
): Ui<BackupAndRestoreScreen> {
  BottomSheet {
    Subheader { Text("Backup/Restore") }

    SectionListItem(
      sectionType = SectionType.FIRST,
      onClick = scopedAction {
        catch { createBackup() }
          .onFailure {
            it.printStackTrace()
            showToast("Failed to backup your data!")
          }
      },
      title = { Text("Export your data") },
      trailing = { Icon(Icons.Default.Save, null) }
    )

    SectionListItem(
      sectionType = SectionType.LAST,
      onClick = scopedAction {
        catch { restoreBackup() }
          .onFailure {
            it.printStackTrace()
            showToast("Failed to restore your data!")
          }
      },
      title = { Text("Restore your data") },
      trailing = { Icon(Icons.Default.Restore, null) }
    )
  }
}
