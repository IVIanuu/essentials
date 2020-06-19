package com.ivianuu.essentials.backup

import androidx.compose.Composable
import androidx.ui.foundation.VerticalScroller
import com.github.michaelbull.result.onFailure
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.viewmodel.ViewModel
import com.ivianuu.essentials.ui.viewmodel.viewModel
import com.ivianuu.essentials.util.Toaster
import com.ivianuu.injekt.Provider
import com.ivianuu.injekt.Transient
import kotlinx.coroutines.launch

@Transient
class BackupAndRestorePage internal constructor(
    private val viewModelFactory: @Provider () -> BackupAndRestoreViewModel
) {
    @Composable
    operator fun invoke() {
        val viewModel = viewModel(init = viewModelFactory)
        Scaffold(
            topBar = { TopAppBar(title = { Text(R.string.es_backup_title) }) }
        ) {
            VerticalScroller {
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

@Transient
internal class BackupAndRestoreViewModel(
    private val backupDataUseCase: BackupDataUseCase,
    private val restoreDataUseCase: RestoreDataUseCase,
    private val toaster: Toaster
) : ViewModel() {

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
