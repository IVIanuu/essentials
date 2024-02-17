/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.backup

import com.ivianuu.essentials.data.*
import com.ivianuu.injekt.*
import java.io.*

@Tag annotation class BackupFileTag {
  @Provide companion object {
    @Provide fun backupPrefs(prefsDir: PrefsDir): BackupFile = prefsDir

    @Provide fun backupDatabases(dataDir: DataDir): BackupFile = dataDir.resolve("databases")

    @Provide fun backupSharedPrefs(dataDir: DataDir): BackupFile = dataDir.resolve("shared_prefs")
  }
}
typealias BackupFile = @BackupFileTag File

@Tag annotation class BackupDirTag {
  @Provide companion object {
    @Provide fun backupDir(dataDir: DataDir): BackupDir = dataDir.resolve("files/backups")
  }
}
typealias BackupDir = @BackupDirTag File
