package com.ivianuu.essentials.backup

import com.ivianuu.essentials.data.DataDir
import com.ivianuu.essentials.data.PrefsDir
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.SetElements
import com.ivianuu.injekt.merge.ApplicationComponent
import com.ivianuu.injekt.merge.BindingModule
import java.io.File

@BindingModule(ApplicationComponent::class)
annotation class BackupFile {
    @Module
    class ModuleImpl<T : File> {
        @SetElements
        fun intoSet(instance: T): BackupFiles = setOf(instance)
    }
}

typealias BackupDir = File

@Binding
fun backupDir(dataDir: DataDir): BackupDir = dataDir.resolve("files/backups")

typealias BackupFiles = Set<File>

@BackupFile
fun backupPrefs(prefsDir: PrefsDir) = prefsDir

@BackupFile
fun backupDatabases(dataDir: DataDir) = dataDir.resolve("databases")
