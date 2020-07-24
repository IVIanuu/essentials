package com.ivianuu.essentials.backup

import androidx.compose.Composable
import com.github.michaelbull.result.onFailure
import com.ivianuu.essentials.backup.BackupAndRestoreAction.BackupClicked
import com.ivianuu.essentials.backup.BackupAndRestoreAction.RestoreClicked
import com.ivianuu.essentials.store.onEachAction
import com.ivianuu.essentials.store.store
import com.ivianuu.essentials.ui.common.InsettingScrollableColumn
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.store.component1
import com.ivianuu.essentials.ui.store.component2
import com.ivianuu.essentials.ui.store.rememberStore
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.essentials.util.exhaustive
import com.ivianuu.injekt.Reader
import kotlinx.coroutines.CoroutineScope

@Reader
@Composable
fun BackupAndRestorePage() {
    val (_, dispatch) = rememberStore { backupAndRestorePage() }
    Scaffold(
        topBar = { TopAppBar(title = { Text(R.string.es_backup_title) }) }
    ) {
        InsettingScrollableColumn {
            ListItem(
                title = { Text(R.string.es_pref_backup) },
                subtitle = { Text(R.string.es_pref_backup_summary) },
                onClick = { dispatch(BackupClicked) }
            )

            ListItem(
                title = { Text(R.string.es_pref_restore) },
                subtitle = { Text(R.string.es_pref_restore_summary) },
                onClick = { dispatch(RestoreClicked) }
            )
        }
    }
}

@Reader
private fun CoroutineScope.backupAndRestorePage() =
    store<BackupAndRestoreState, BackupAndRestoreAction>(BackupAndRestoreState) {
        onEachAction { action ->
            when (action) {
                BackupClicked -> {
                    backupData()
                        .onFailure {
                            it.printStackTrace()
                            Toaster.toast(R.string.es_backup_error)
                        }
                }
                RestoreClicked -> {
                    restoreData()
                        .onFailure {
                            it.printStackTrace()
                            Toaster.toast(R.string.es_restore_error)
                        }
                }
            }.exhaustive
        }
    }

private object BackupAndRestoreState

private sealed class BackupAndRestoreAction {
    object BackupClicked : BackupAndRestoreAction()
    object RestoreClicked : BackupAndRestoreAction()
}
