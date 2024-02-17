/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.notificationlistener

import com.ivianuu.essentials.*

data object NotificationScope

val Scope<*>.notificationListenerService: EsNotificationListenerService
  get() = service()
