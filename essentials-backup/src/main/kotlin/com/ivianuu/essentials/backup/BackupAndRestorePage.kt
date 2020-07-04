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
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.Unscoped
import kotlinx.coroutines.launch

@Unscoped
class BackupAndRestorePage internal constructor(
    private val viewModelFactory: @Provider () -> BackupAndRestoreViewModel
) {
    @Composable
    operator fun invoke() {
        val viewModel = viewModel(init = viewModelFactory)
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
}

@Unscoped
internal class BackupAndRestoreViewModel(
    private val backupDataUseCase: BackupDataUseCase,
    dispatchers: AppCoroutineDispatchers,
    private val restoreDataUseCase: RestoreDataUseCase,
    private val toaster: Toaster
) : ViewModel(dispatchers) {

    fun backupClicked() {
        scope.launch {
            backupDataUseCase()
                .onFailure {
                    it.printStackTrace()
                    toaster.toast(R.string.es_backup_error)
                }
        }
    }

    fun restoreClicked() {
        scope.launch {
            restoreDataUseCase()
                .onFailure {
                    it.printStackTrace()
                    toaster.toast(R.string.es_restore_error)
                }
        }
    }

}
