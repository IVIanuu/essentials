package com.ivianuu.essentials.sample.ui

import com.ivianuu.essentials.backup.BackupAndRestoreKey
import com.ivianuu.injekt.Given

@HomeItemBinding
@Given
val backupHomeItem = HomeItem("Backup and restore") { BackupAndRestoreKey() }
