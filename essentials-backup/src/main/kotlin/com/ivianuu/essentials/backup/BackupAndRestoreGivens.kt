package com.ivianuu.essentials.backup

import com.ivianuu.essentials.data.DataDir
import com.ivianuu.essentials.data.PrefsDir
import com.ivianuu.injekt.Effect
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenSetElements
import com.ivianuu.injekt.given
import java.io.File

@Effect
annotation class BackupFile {
    companion object {
        @GivenSetElements
        fun <T : () -> File> intoSet(): BackupFiles = setOf(given<T>())
    }
}

object BackupAndRestoreGivens {

    @Given
    fun backupDir(): BackupDir = given<DataDir>().resolve("files/backups")

    @BackupFile
    fun backupPrefs() = given<PrefsDir>()

    @BackupFile
    fun backupDatabases() = given<DataDir>().resolve("databases")

}

internal typealias BackupDir = File

typealias BackupFiles = Set<() -> File>
