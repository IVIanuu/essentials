package com.ivianuu.essentials.sample.ui

import com.ivianuu.essentials.backup.*
import com.ivianuu.injekt.*

@Provide val backupHomeItem = HomeItem("Backup and restore") { BackupAndRestoreKey }
