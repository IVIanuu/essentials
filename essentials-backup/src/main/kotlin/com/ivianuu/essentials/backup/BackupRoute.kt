package com.ivianuu.essentials.backup

import androidx.ui.res.stringResource
import com.ivianuu.essentials.ui.common.ScrollableScreen
import com.ivianuu.essentials.ui.common.launchOnClick
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.navigation.Route

val BackupRoute = Route {
    ScrollableScreen(title = stringResource(R.string.es_backup_title)) {
        val backupData = inject<BackupData>()
        ListItem(
            title = { Text(R.string.es_pref_backup) },
            subtitle = { Text(R.string.es_pref_backup_summary) },
            onClick = launchOnClick { backupData.invoke() }
        )

        val restoreData = inject<RestoreData>()
        ListItem(
            title = { Text(R.string.es_pref_restore) },
            subtitle = { Text(R.string.es_pref_restore_summary) },
            onClick = launchOnClick { restoreData.invoke() }
        )
    }
}
