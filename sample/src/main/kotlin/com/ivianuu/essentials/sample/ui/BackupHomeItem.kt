/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import com.ivianuu.essentials.backup.*
import com.ivianuu.injekt.*

@Provide val backupHomeItem = HomeItem("Backup and restore") { BackupAndRestoreKey }
