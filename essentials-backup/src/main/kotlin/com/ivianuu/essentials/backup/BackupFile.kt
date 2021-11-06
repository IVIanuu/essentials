/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.backup

import com.ivianuu.essentials.data.DataDir
import com.ivianuu.essentials.data.PrefsDir
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import java.io.File

@Tag annotation class BackupFileTag
typealias BackupFile = @BackupFileTag File

@Tag annotation class BackupDirTag
typealias BackupDir = @BackupDirTag File

@Provide fun backupDir(dataDir: DataDir): BackupDir = dataDir.resolve("files/backups")

@Provide fun backupPrefs(prefsDir: PrefsDir): BackupFile = prefsDir

@Provide fun backupDatabases(dataDir: DataDir): BackupFile = dataDir.resolve("databases")

@Provide fun backupSharedPrefs(dataDir: DataDir): BackupFile = dataDir.resolve("shared_prefs")
