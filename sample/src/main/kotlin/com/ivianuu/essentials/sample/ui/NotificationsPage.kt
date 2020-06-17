package com.ivianuu.essentials.sample.ui

import android.app.Notification
import androidx.compose.Composable
import androidx.compose.remember
import androidx.ui.core.Alignment
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.Image
import androidx.ui.foundation.Text
import androidx.ui.foundation.drawBackground
import androidx.ui.foundation.shape.corner.CircleShape
import androidx.ui.graphics.Color
import androidx.ui.layout.Arrangement
import androidx.ui.layout.Column
import androidx.ui.layout.Spacer
import androidx.ui.layout.fillMaxSize
import androidx.ui.layout.height
import androidx.ui.layout.padding
import androidx.ui.layout.size
import androidx.ui.material.Button
import androidx.ui.material.IconButton
import androidx.ui.material.MaterialTheme
import androidx.ui.material.icons.Icons
import androidx.ui.material.icons.filled.Clear
import androidx.ui.material.icons.filled.Error
import androidx.ui.unit.dp
import com.ivianuu.essentials.notificationlistener.DefaultNotificationListenerService
import com.ivianuu.essentials.notificationlistener.NotificationStore
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.PermissionManager
import com.ivianuu.essentials.permission.Title
import com.ivianuu.essentials.permission.notificationlistener.NotificationListenerPermission
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.essentials.ui.collectAsAsync
import com.ivianuu.essentials.ui.common.AsyncBox
import com.ivianuu.essentials.ui.common.AsyncLazyColumnItems
import com.ivianuu.essentials.ui.common.launchOnClick
import com.ivianuu.essentials.ui.image.Icon
import com.ivianuu.essentials.ui.image.toImageAsset
import com.ivianuu.essentials.ui.launchAsync
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.injekt.Transient
import kotlinx.coroutines.withContext

@Transient
class NotificationsPage(
    private val dispatchers: AppCoroutineDispatchers,
    private val notificationStore: NotificationStore,
    private val permissionManager: PermissionManager
) {

    @Composable
    operator fun invoke() {
        Scaffold(
            topAppBar = { TopAppBar(title = { Text("Notifications") }) },
            body = {
                val notificationPermission = remember {
                    NotificationListenerPermission(
                        DefaultNotificationListenerService::class,
                        Permission.Title withValue "Notifications"
                    )
                }

                AsyncBox(
                    state = remember {
                        permissionManager.hasPermissions(notificationPermission)
                    }.collectAsAsync(),
                    success = { hasPermission ->
                        if (hasPermission) {
                            AsyncLazyColumnItems(
                                state = notificationStore.notifications.collectAsAsync(),
                                successEmpty = {
                                    Text(
                                        text = "No notifications",
                                        style = MaterialTheme.typography.subtitle1,
                                        modifier = Modifier.center()
                                    )
                                },
                                successItemContent = { sbn ->
                                    ListItem(
                                        title = {
                                            Text(
                                                sbn.notification.extras.getString(Notification.EXTRA_TITLE)
                                                    ?: ""
                                            )
                                        },
                                        subtitle = {
                                            Text(
                                                sbn.notification.extras.getString(Notification.EXTRA_TEXT)
                                                    ?: ""
                                            )
                                        },
                                        onClick = launchOnClick {
                                            notificationStore.openNotification(sbn.notification)
                                        },
                                        leading = {
                                            val context = ContextAmbient.current
                                            AsyncBox(
                                                modifier = Modifier.size(40.dp)
                                                    .drawBackground(
                                                        color = Color(sbn.notification.color),
                                                        shape = CircleShape
                                                    )
                                                    .padding(all = 8.dp),
                                                state = launchAsync {
                                                    withContext(dispatchers.io) {
                                                        sbn.notification.smallIcon
                                                            .loadDrawable(context)
                                                            .toImageAsset()
                                                    }
                                                },
                                                success = {
                                                    Image(
                                                        modifier = Modifier.size(24.dp),
                                                        asset = it
                                                    )
                                                },
                                                fail = {
                                                    Icon(Icons.Default.Error)
                                                }
                                            )
                                        },
                                        trailing = if (sbn.isClearable) {
                                            {
                                                IconButton(
                                                    onClick = launchOnClick {
                                                        notificationStore.dismissNotification(sbn.key)
                                                    }
                                                ) {
                                                    Icon(Icons.Default.Clear)
                                                }
                                            }
                                        } else null
                                    )
                                }
                            )
                        } else {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalGravity = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Permissions required",
                                    style = MaterialTheme.typography.subtitle1
                                )
                                Spacer(Modifier.height(8.dp))
                                Button(
                                    onClick = launchOnClick {
                                        permissionManager.request(notificationPermission)
                                    }
                                ) {
                                    Text("Request")
                                }
                            }
                        }
                    }
                )
            }
        )
    }

}
