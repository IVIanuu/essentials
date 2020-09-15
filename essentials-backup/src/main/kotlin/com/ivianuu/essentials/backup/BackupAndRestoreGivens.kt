package com.ivianuu.essentials.backup

import com.ivianuu.essentials.data.DataDir
import com.ivianuu.essentials.data.PrefsDir
import com.ivianuu.injekt.ContextBuilder
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Key
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.common.Adapter
import com.ivianuu.injekt.given
import com.ivianuu.injekt.keyOf
import java.io.File

@Adapter
annotation class BackupFile {
    companion object : Adapter.Impl<() -> File> {
        override fun ContextBuilder.configure(
            key: Key<() -> File>,
            provider: @Reader () -> () -> File
        ) {
            set(keyOf<BackupFiles>()) {
                add(key, elementProvider = provider)
            }
        }
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
