package essentials.sample.ui

import android.app.*
import androidx.compose.animation.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import essentials.compose.*
import essentials.notificationlistener.*
import essentials.permission.*
import essentials.ui.common.*
import essentials.ui.material.*
import essentials.ui.navigation.*
import injekt.*

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
