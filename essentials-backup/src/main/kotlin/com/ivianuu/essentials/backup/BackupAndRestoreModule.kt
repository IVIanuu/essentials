package com.ivianuu.essentials.backup

import com.ivianuu.essentials.data.DataDir
import com.ivianuu.injekt.ApplicationComponent
import com.ivianuu.injekt.Distinct
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given

object BackupAndRestoreModule {

    @Given
    @Reader
    fun backupDir() = "${given<DataDir>()}/backups"

}

@Distinct
internal typealias BackupDir = String
