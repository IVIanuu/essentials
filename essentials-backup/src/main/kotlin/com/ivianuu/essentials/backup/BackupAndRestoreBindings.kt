/*
 * Copyright 2020 Manuel Wrage
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
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.GivenSetElement
import com.ivianuu.injekt.Macro
import com.ivianuu.injekt.Qualifier
import java.io.File

@Qualifier
annotation class BackupFileBinding

@Macro
@GivenSetElement
fun <T : @BackupFileBinding File> backupFileBindingImpl(@Given instance: T): BackupFile = instance

typealias BackupFile = File

typealias BackupDir = File

@Given
fun backupDir(@Given dataDir: DataDir): BackupDir =
    dataDir.resolve("files/backups")

@BackupFileBinding
@Given
fun backupPrefs(@Given prefsDir: PrefsDir) = prefsDir

@BackupFileBinding
@Given
fun backupDatabases(@Given dataDir: DataDir) =
    dataDir.resolve("databases")
