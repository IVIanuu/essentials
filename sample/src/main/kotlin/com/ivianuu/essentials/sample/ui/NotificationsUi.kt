/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.sample.ui

import android.app.Notification
import android.service.notification.NotificationListenerService
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.getOrNull
import com.ivianuu.essentials.map
import com.ivianuu.essentials.notificationlistener.EsNotificationListenerService
import com.ivianuu.essentials.notificationlistener.NotificationService
import com.ivianuu.essentials.permission.PermissionRequester
import com.ivianuu.essentials.permission.PermissionState
import com.ivianuu.essentials.permission.notificationlistener.NotificationListenerPermission
import com.ivianuu.essentials.recover
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.state.action
import com.ivianuu.essentials.state.bind
import com.ivianuu.essentials.state.bindResource
import com.ivianuu.essentials.ui.image.toImageBitmap
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.resource.ResourceBox
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

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
      .bind { emptyList() }
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
      .getOrNull()
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
  override val icon: (@Composable () -> Unit)?
    get() = null
}
