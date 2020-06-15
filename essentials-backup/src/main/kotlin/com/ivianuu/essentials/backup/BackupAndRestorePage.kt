package com.ivianuu.essentials.backup

import androidx.compose.Composable
import androidx.ui.foundation.VerticalScroller
import com.ivianuu.essentials.ui.common.RetainedScrollerPosition
import com.ivianuu.essentials.ui.common.launchOnClick
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.injekt.Transient

@Transient
class BackupAndRestorePage internal constructor(
    private val backupData: BackupDataUseCase,
    private val restoreData: RestoreDataUseCase
) {
    @Composable
    operator fun invoke() {
        Scaffold(
            topAppBar = { TopAppBar(title = { Text(R.string.es_backup_title) }) },
            body = {
                VerticalScroller(scrollerPosition = RetainedScrollerPosition()) {
                    ListItem(
                        title = { Text(R.string.es_pref_backup) },
                        subtitle = { Text(R.string.es_pref_backup_summary) },
                        onClick = launchOnClick { backupData() }
                    )

                    ListItem(
                        title = { Text(R.string.es_pref_restore) },
                        subtitle = { Text(R.string.es_pref_restore_summary) },
                        onClick = launchOnClick { restoreData() }
                    )
                }
            }
        )
    }
}
