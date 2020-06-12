package com.ivianuu.essentials.backup

import androidx.ui.res.stringResource
import com.ivianuu.essentials.ui.common.ScrollableScreen
import com.ivianuu.essentials.ui.common.launchOnClick
import com.ivianuu.essentials.ui.core.Text
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.prefs.SimplePreference

val BackupRoute = Route {
    ScrollableScreen(title = stringResource(R.string.es_backup_title)) {
        val backupData = inject<BackupData>()
        SimplePreference(
            title = { Text(R.string.es_pref_backup) },
            summary = { Text(R.string.es_pref_backup_summary) },
            onClick = launchOnClick { backupData.invoke() }
        )

        val restoreData = inject<RestoreData>()
        SimplePreference(
            title = { Text(R.string.es_pref_restore) },
            summary = { Text(R.string.es_pref_restore_summary) },
            onClick = launchOnClick { restoreData.invoke() }
        )
    }
}
