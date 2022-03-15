/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import android.app.*
import android.service.notification.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import androidx.core.graphics.drawable.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.notificationlistener.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.notificationlistener.*
import com.ivianuu.essentials.resource.*
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.state.*
import com.ivianuu.essentials.ui.image.*
import com.ivianuu.essentials.ui.layout.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.resource.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.reflect.*

@Provide val notificationsHomeItem = HomeItem("Notifications") { NotificationsKey }

object NotificationsKey : Key<Unit>

@Provide val notificationsUi = ModelKeyUi<NotificationsKey, NotificationsModel> {
  Scaffold(topBar = { TopAppBar(title = { Text("Notifications") }) }) {
    ResourceBox(hasPermissions) { hasPermission ->
      if (hasPermission) {
        NotificationsList(
          notifications = notifications,
          onNotificationClick = { openNotification(it) },
          onDismissNotificationClick = { dismissNotification(it) }
        )
      } else {
        NotificationPermissions(requestPermissions)
      }
    }
  }
}

@Composable private fun NotificationsList(
  onNotificationClick: (UiNotification) -> Unit,
  onDismissNotificationClick: (UiNotification) -> Unit,
  notifications: List<UiNotification>
) {
  if (notifications.isEmpty()) {
    Text(
      text = "No notifications",
      style = MaterialTheme.typography.subtitle1,
      modifier = Modifier.center()
    )
  } else {
    LazyColumn {
      items(notifications) { notification ->
        ListItem(
          modifier = Modifier.clickable { onNotificationClick(notification) },
          title = { Text(notification.title) },
          subtitle = { Text(notification.text) },
          leading = {
            Box(
              modifier = Modifier.size(40.dp)
                .background(
                  color = notification.color,
                  shape = CircleShape
                )
                .padding(all = 8.dp)
            ) {
              notification.icon()
            }
          },
          trailing = if (notification.isClearable) {
            {
              IconButton(onClick = { onDismissNotificationClick(notification) }) {
                Icon(R.drawable.es_ic_clear)
              }
            }
          } else null
        )
      }
    }
  }
}

@Composable private fun NotificationPermissions(onRequestPermissionsClick: () -> Unit) {
  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(
      text = "Permissions required",
      style = MaterialTheme.typography.subtitle1
    )
    Spacer(Modifier.height(8.dp))
    Button(onClick = onRequestPermissionsClick) {
      Text("Request")
    }
  }
}

data class NotificationsModel(
  val hasPermissions: Resource<Boolean>,
  val notifications: List<UiNotification>,
  val requestPermissions: () -> Unit,
  val openNotification: (UiNotification) -> Unit,
  val dismissNotification: (UiNotification) -> Unit
)

data class UiNotification(
  val title: String,
  val text: String,
  val icon: @Composable () -> Unit,
  val color: Color,
  val isClearable: Boolean,
  val sbn: StatusBarNotification
)

@Provide fun notificationsModel(
  permissionState: Flow<PermissionState<SampleNotificationsPermission>>,
  permissionRequester: PermissionRequester,
  service: NotificationService,
  C: AppContext,
  S: NamedCoroutineScope<KeyUiScope>
) = Model {
  NotificationsModel(
    hasPermissions = permissionState.bindResource(),
    notifications = service.notifications
      .bind(emptyList())
      .map { it.toUiNotification() },
    requestPermissions = action {
      permissionRequester(listOf(typeKeyOf<SampleNotificationsPermission>()))
    },
    openNotification = action { notification ->
      service.openNotification(notification.sbn.notification)
    },
    dismissNotification = action { notification ->
      service.dismissNotification(notification.sbn.key)
    }
  )
}

private fun StatusBarNotification.toUiNotification(@Inject C: AppContext) = UiNotification(
  title = notification.extras.getCharSequence(Notification.EXTRA_TITLE)
    ?.toString() ?: "",
  text = notification.extras.getCharSequence(Notification.EXTRA_TEXT)
    ?.toString() ?: "",
  icon = { NotificationIcon(notification) },
  color = Color(notification.color),
  isClearable = isClearable,
  sbn = this
)

@Composable private fun NotificationIcon(
  notification: Notification,
  @Inject context: AppContext
) {
  val icon by produceState<ImageBitmap?>(null) {
    value = catch {
      notification.smallIcon
        .loadDrawable(context)
    }.recover {
      notification.getLargeIcon()
        .loadDrawable(context)
    }
      .map { it.toBitmap().toImageBitmap() }
      .getOrElse { null }
  }

  Image(
    modifier = Modifier.size(24.dp),
    bitmap = icon ?: return
  )
}

@Provide object SampleNotificationsPermission : NotificationListenerPermission {
  override val serviceClass: KClass<out NotificationListenerService>
    get() = EsNotificationListenerService::class
  override val title: String
    get() = "Notifications"
}
