/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.notificationlistener

import essentials.*
import injekt.*

@Provide data object NotificationScope : ChildScopeMarker<NotificationScope, AppScope>

val Scope<*>.notificationListenerService: EsNotificationListenerService
  get() = service()
