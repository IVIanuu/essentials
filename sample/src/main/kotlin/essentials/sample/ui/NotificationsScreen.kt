package essentials.sample.ui

import android.app.*
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
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
  api: NotificationsApi,
  permissions: Permissions,
  context: ScreenContext<NotificationsScreen> = inject
): Ui<NotificationsScreen> {
  EsScaffold(
    topBar = { EsAppBar { Text("Notifications") } },
    floatingActionButton = {
      AnimatedVisibility(api is NotificationsApi.Notifications) {
        if (api is NotificationsApi.Notifications)
          FloatingActionButton(onClick = action { api.dismissAllNotifications() }) {
            Icon(Icons.Default.ClearAll, null)
        }
      }
    }
  ) {
    when (api) {
      is NotificationsApi.Unavailable -> {
        LaunchedEffect(true) {
          permissions.ensurePermissions(listOf(SampleNotificationListenerPermission::class))
        }

        Text("Unavailable", modifier = Modifier.padding(LocalContentPadding.current))
      }
      is NotificationsApi.Empty -> Text("No notifications", modifier = Modifier.padding(LocalContentPadding.current))
      is NotificationsApi.Notifications -> EsLazyColumn {
        section {
          sectionItems(api.notifications) { notification, _ ->
            SectionListItem(
              onClick = action { api.openNotification(notification) },
              title = {
                Text(
                  notification.notification.extras.getCharSequence(Notification.EXTRA_TITLE)
                    ?.toString() ?: ""
                )
              },
              description = {
                Text(
                  notification.notification.extras.getCharSequence(Notification.EXTRA_TEXT)
                    ?.toString() ?: ""
                )
              },
              trailing = {
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
}
