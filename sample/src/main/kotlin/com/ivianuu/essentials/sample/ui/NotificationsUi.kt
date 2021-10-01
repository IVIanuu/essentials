/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.sample.ui

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.catch
import com.ivianuu.essentials.coroutines.parMap
import com.ivianuu.essentials.fold
import com.ivianuu.essentials.map
import com.ivianuu.essentials.notificationlistener.EsNotificationListenerService
import com.ivianuu.essentials.notificationlistener.NotificationService
import com.ivianuu.essentials.optics.Optics
import com.ivianuu.essentials.permission.PermissionRequester
import com.ivianuu.essentials.permission.PermissionState
import com.ivianuu.essentials.permission.notificationlistener.NotificationListenerPermission
import com.ivianuu.essentials.recover
import com.ivianuu.essentials.resource.Idle
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.flowAsResource
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.ui.image.toImageBitmap
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.Button
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiScope
import com.ivianuu.essentials.ui.navigation.ModelKeyUi
import com.ivianuu.essentials.ui.resource.ResourceBox
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnFor
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.typeKeyOf
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlin.reflect.KClass

@Provide val notificationsHomeItem = HomeItem("Notifications") { NotificationsKey }

object NotificationsKey : Key<Unit>

@Provide val notificationsUi: ModelKeyUi<NotificationsKey, NotificationsModel> = {
  Scaffold(topBar = { TopAppBar(title = { Text("Notifications") }) }) {
    ResourceBox(model.hasPermissions) { hasPermission ->
      if (hasPermission) {
        NotificationsList(
          notifications = model.notifications,
          onNotificationClick = { model.openNotification(it) },
          onDismissNotificationClick = { model.dismissNotification(it) }
        )
      } else {
        NotificationPermissions(model.requestPermissions)
      }
    }
  }
}

@Composable private fun NotificationsList(
  onNotificationClick: (UiNotification) -> Unit,
  onDismissNotificationClick: (UiNotification) -> Unit,
  notifications: Resource<List<UiNotification>>
) {
  ResourceLazyColumnFor(
    resource = notifications,
    successEmpty = {
      Text(
        text = "No notifications",
        style = MaterialTheme.typography.subtitle1,
        modifier = Modifier.center()
      )
    },
    successItemContent = { notification ->
      ListItem(
        title = { Text(notification.title) },
        subtitle = { Text(notification.text) },
        onClick = { onNotificationClick(notification) },
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
  )
}

@Composable private fun NotificationPermissions(
  onRequestPermissionsClick: () -> Unit
) {
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

@Optics data class NotificationsModel(
  val hasPermissions: Resource<Boolean> = Idle,
  val notifications: Resource<List<UiNotification>> = Idle,
  val requestPermissions: () -> Unit = {},
  val openNotification: (UiNotification) -> Unit = {},
  val dismissNotification: (UiNotification) -> Unit = {}
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
  context: AppContext,
  permissionState: Flow<PermissionState<SampleNotificationsPermission>>,
  permissionRequester: PermissionRequester,
  scope: NamedCoroutineScope<KeyUiScope>,
  service: NotificationService
): StateFlow<NotificationsModel> = scope.state(NotificationsModel()) {
  service.notifications
    .map { notifications ->
      notifications
        .parMap { it.toUiNotification(context) }
    }
    .flowAsResource()
    .update { copy(notifications = it) }
  permissionState
    .flowAsResource()
    .update { copy(hasPermissions = it) }
  action(NotificationsModel.requestPermissions()) {
    permissionRequester(listOf(typeKeyOf<SampleNotificationsPermission>()))
  }
  action(NotificationsModel.openNotification()) { notification ->
    service.openNotification(notification.sbn.notification)
  }
  action(NotificationsModel.dismissNotification()) { notification ->
    service.dismissNotification(notification.sbn.key)
  }
}

private fun StatusBarNotification.toUiNotification(context: AppContext) = UiNotification(
  title = notification.extras.getCharSequence(Notification.EXTRA_TITLE)
    ?.toString() ?: "",
  text = notification.extras.getCharSequence(Notification.EXTRA_TEXT)
    ?.toString() ?: "",
  icon = catch {
    notification.smallIcon
      .loadDrawable(context)
  }.recover {
    notification.getLargeIcon()
      .loadDrawable(context)
  }
    .map { it.toBitmap().toImageBitmap() }
    .fold(
      success = { bitmap ->
        {
          Image(
            modifier = Modifier.size(24.dp),
            bitmap = bitmap
          )
        }
      },
      failure = {
        { Icon(R.drawable.es_ic_error) }
      }
    ),
  color = Color(notification.color),
  isClearable = isClearable,
  sbn = this
)

@Provide object SampleNotificationsPermission : NotificationListenerPermission {
  override val serviceClass: KClass<out NotificationListenerService>
    get() = EsNotificationListenerService::class
  override val title: String = "Notifications"
  override val icon: @Composable (() -> Unit)?
    get() = null
}
