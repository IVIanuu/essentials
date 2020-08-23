package com.ivianuu.essentials.backup

import com.ivianuu.essentials.data.DataDir
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.given

object BackupAndRestoreGivens {

    @Given
    fun backupDir(): BackupDir = "${given<DataDir>()}/backups"

}

internal typealias BackupDir = String
