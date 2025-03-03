/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.sample.ui

import essentials.backup.*
import injekt.*

@Provide val backupHomeItem = HomeItem("Backup and restore") { BackupAndRestoreScreen() }
