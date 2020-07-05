package com.ivianuu.essentials.backup

import androidx.compose.Composable
import com.github.michaelbull.result.onFailure
import com.ivianuu.essentials.ui.common.InsettingScrollableColumn
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.viewmodel.ViewModel
import com.ivianuu.essentials.ui.viewmodel.viewModel
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.Unscoped
import com.ivianuu.injekt.get
import kotlinx.coroutines.launch

@Reader
@Composable
fun BackupAndRestorePage() {
    val viewModel = viewModel<BackupAndRestoreViewModel>()
    Scaffold(
        topBar = { TopAppBar(title = { Text(R.string.es_backup_title) }) }
    ) {
        InsettingScrollableColumn {
            ListItem(
                title = { Text(R.string.es_pref_backup) },
                subtitle = { Text(R.string.es_pref_backup_summary) },
                onClick = { viewModel.backupClicked() }
            )

            ListItem(
                title = { Text(R.string.es_pref_restore) },
                subtitle = { Text(R.string.es_pref_restore_summary) },
                onClick = { viewModel.restoreClicked() }
            )
        }
    }
}

@Reader
@Unscoped
internal class BackupAndRestoreViewModel : ViewModel() {

    fun backupClicked() {
        scope.launch {
            backupData()
                .onFailure {
                    it.printStackTrace()
                    Toaster.toast(R.string.es_backup_error)
                }
        }
    }

    fun restoreClicked() {
        scope.launch {
            restoreData()
                .onFailure {
                    it.printStackTrace()
                    Toaster.toast(R.string.es_restore_error)
                }
        }
    }

}
