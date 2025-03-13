package essentials.sample.ui

import android.app.Notification
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import essentials.compose.action
import essentials.notificationlistener.NotificationApi
import essentials.permission.PermissionManager
import essentials.ui.common.EsLazyColumn
import essentials.ui.material.EsAppBar
import essentials.ui.material.EsListItem
import essentials.ui.material.EsScaffold
import essentials.ui.navigation.Screen
import essentials.ui.navigation.Ui
import injekt.Provide

@Provide val notificationsHomeItem = HomeItem("Notifications") {
  NotificationsScreen()
}

class NotificationsScreen : Screen<Unit>

@Provide @Composable fun NotificationsUi(
  api: NotificationApi,
  permissionManager: PermissionManager
): Ui<NotificationsScreen> {
  EsScaffold(
    topBar = { EsAppBar { Text("Notifications") } },
    floatingActionButton = {
      AnimatedVisibility(api is NotificationApi.Notifications) {
        if (api is NotificationApi.Notifications)
          FloatingActionButton(onClick = action { api.dismissAllNotifications() }) {
            Icon(Icons.Default.ClearAll, null)
        }
      }
    }
  ) {
    when (api) {
      is NotificationApi.Unavailable -> {
        LaunchedEffect(true) {
          permissionManager.ensurePermissions(listOf(SampleNotificationListenerPermission::class))
        }

        Text("Unavailable")
      }
      is NotificationApi.Empty -> Text("No notifications")
      is NotificationApi.Notifications -> EsLazyColumn {
        items(api.notifications) { notification ->
          EsListItem(
            onClick = action { api.openNotification(notification.notification) },
            headlineContent = {
              Text(
                notification.notification.extras.getCharSequence(Notification.EXTRA_TITLE)
                  ?.toString() ?: ""
              )
            },
            supportingContent = {
              Text(
                notification.notification.extras.getCharSequence(Notification.EXTRA_TEXT)
                  ?.toString() ?: ""
              )
            },
            trailingContent = {
              IconButton(
                onClick = action { api.dismissNotification(notification.key) }
              ) {
                Icon(Icons.Default.Clear, null)
              }
            }
          )
        }
      }
    }
  }
}
