package com.ivianuu.essentials.backup

import com.ivianuu.essentials.data.DataDir
import com.ivianuu.injekt.Distinct
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.given

object BackupAndRestoreModule {

    @Given
    fun backupDir(): BackupDir = "${given<DataDir>()}/backups"

}

@Distinct
internal typealias BackupDir = String
