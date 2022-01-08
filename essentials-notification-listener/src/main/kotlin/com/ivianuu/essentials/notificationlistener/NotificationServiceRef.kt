/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.notificationlistener

import com.ivianuu.injekt.*
import kotlinx.coroutines.flow.*

@Provide val notificationListenerRef = MutableStateFlow<EsNotificationListenerService?>(null)
