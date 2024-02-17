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
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.*
import androidx.compose.ui.unit.*
import androidx.core.graphics.drawable.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.compose.*
import com.ivianuu.essentials.notificationlistener.*
import com.ivianuu.essentials.permission.*
import com.ivianuu.essentials.permission.notificationlistener.*
import com.ivianuu.essentials.resource.*
import com.ivianuu.essentials.ui.layout.*
import com.ivianuu.essentials.ui.material.*
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.essentials.ui.resource.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import kotlinx.coroutines.flow.*

@Provide val notificationsHomeItem = HomeItem("Notifications") { NotificationsScreen() }

class NotificationsScreen : Screen<Unit>

@Provide val notificationsUi = Ui<NotificationsScreen, NotificationsState> { state ->
  ScreenScaffold(topBar = { AppBar { Text("Notifications") } }) {
    ResourceBox(state.hasPermissions) { hasPermission ->
      if (hasPermission) {
        if (state.notifications.isEmpty()) {
          Text(
            text = "No notifications",
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.center()
          )
        } else {
          LazyColumn {
            items(state.notifications) { notification ->
              ListItem(
                onClick = { state.openNotification(notification) },
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
                    IconButton(onClick = { state.dismissNotification(notification) }) {
                      Icon(painterResource(com.ivianuu.essentials.gestures.R.drawable.ic_clear), null)
                    }
                  }
                } else null
              )
            }
          }
        }
      } else {
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
          Button(onClick = state.requestPermissions) { Text("Request") }
        }
      }
    }
  }
}

data class NotificationsState(
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

@Provide fun notificationsPresenter(
  @Inject appContext: AppContext,
  permissionManager: PermissionManager,
  service: NotificationService
) = Presenter {
  NotificationsState(
    hasPermissions = remember {
      permissionManager
        .permissionState(listOf(typeKeyOf<SampleNotificationsPermission>()))
    }
      .collectAsResourceState()
      .value,
    notifications = service.notifications
      .map { it.map { it.toUiNotification() } }
      .collectAsState(emptyList())
      .value,
    requestPermissions = action {
      permissionManager.requestPermissions(listOf(typeKeyOf<SampleNotificationsPermission>()))
    },
    openNotification = action { notification ->
      service.openNotification(notification.sbn.notification)
    },
    dismissNotification = action { notification ->
      service.dismissNotification(notification.sbn.key)
    }
  )
}

private fun StatusBarNotification.toUiNotification(
  @Inject appContext: AppContext
) = UiNotification(
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
  @Inject appContext: AppContext
) {
  val icon = produceState<ImageBitmap?>(null) {
    value = catch {
      notification.smallIcon
        .loadDrawable(appContext)
    }.recover {
      notification.getLargeIcon()
        .loadDrawable(appContext)
    }
      .map { it?.toBitmap()?.asImageBitmap() }
      .getOrNull()
  }.value ?: return

  Image(
    modifier = Modifier.size(24.dp),
    bitmap = icon,
    contentDescription = null
  )
}

@Provide object SampleNotificationsPermission : NotificationListenerPermission(
  serviceClass = EsNotificationListenerService::class,
  title = "Notifications"
)
