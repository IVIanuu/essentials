/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.backup

import androidx.core.content.FileProvider
import com.ivianuu.essentials.AndroidComponent
import com.ivianuu.injekt.Provide

@Provide @AndroidComponent class BackupFileProvider : FileProvider()
