package com.ivianuu.essentials.backup

import androidx.ui.res.stringResource
import com.ivianuu.essentials.ui.common.ScrollableScreen
import com.ivianuu.essentials.ui.common.launchOnClick
import com.ivianuu.essentials.ui.injekt.inject
import com.ivianuu.essentials.ui.navigation.Route
import com.ivianuu.essentials.ui.prefs.SimplePreference

val BackupRoute = Route {
    ScrollableScreen(title = stringResource(R.string.es_backup_title)) {
        val backupUseCase = inject<BackupUseCase>()
        SimplePreference(
            title = stringResource(R.string.es_pref_backup),
            summary = stringResource(R.string.es_pref_backup_summary),
            onClick = launchOnClick { backupUseCase.backup() }
        )

        val restoreUseCase = inject<RestoreUseCase>()
        SimplePreference(
            title = stringResource(R.string.es_pref_restore),
            summary = stringResource(R.string.es_pref_restore_summary),
            onClick = launchOnClick { restoreUseCase.restore() }
        )
    }
}
