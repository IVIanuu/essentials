/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import android.app.Notification
import android.service.notification.StatusBarNotification
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import arrow.core.Either
import arrow.core.catch
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.compose.action
import com.ivianuu.essentials.notificationlistener.EsNotificationListenerService
import com.ivianuu.essentials.notificationlistener.NotificationService
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.permission.notificationlistener.NotificationListenerPermission
import com.ivianuu.essentials.recover
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.collectAsResourceState
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.AppBar
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.ScreenScaffold
import com.ivianuu.essentials.ui.navigation.Presenter
import com.ivianuu.essentials.ui.navigation.Screen
import com.ivianuu.essentials.ui.navigation.Ui
import com.ivianuu.essentials.ui.resource.ResourceBox
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.flow.map

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
                modifier = Modifier.clickable { state.openNotification(notification) },
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
                      Icon(painterResource(R.drawable.es_ic_clear), null)
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
      permissionManager.permissionState(listOf(typeKeyOf<SampleNotificationsPermission>()))
    }.collectAsResourceState().value,
    notifications = remember {
      service.notifications
        .map {
          it.map { it.toUiNotification() }
        }
    }.collectAsState(emptyList()).value,
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
    value = Either.catch {
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
