/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import com.ivianuu.essentials.backup.BackupAndRestoreScreen
import com.ivianuu.injekt.Provide

@Provide val backupHomeItem = HomeItem("Backup and restore") { BackupAndRestoreScreen() }
