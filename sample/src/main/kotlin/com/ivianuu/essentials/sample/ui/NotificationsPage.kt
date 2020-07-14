package com.ivianuu.essentials.sample.ui

import android.app.Notification
import androidx.compose.Composable
import androidx.compose.remember
import androidx.ui.core.Alignment
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.foundation.Icon
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
import com.ivianuu.essentials.ui.coroutines.compositionScope
import com.ivianuu.essentials.ui.image.toImageAsset
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Navigator
import com.ivianuu.essentials.ui.resource.ResourceBox
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnItems
import com.ivianuu.essentials.ui.resource.collectAsResource
import com.ivianuu.essentials.ui.resource.produceResource
import com.ivianuu.essentials.util.AppCoroutineDispatchers
import com.ivianuu.essentials.util.dispatchers
import com.ivianuu.injekt.Reader
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Reader
@Composable
fun NotificationsPage() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Notifications") }) }
    ) {
        val notificationPermission = remember {
            NotificationListenerPermission(
                DefaultNotificationListenerService::class,
                Permission.Title withValue "Notifications"
            )
        }

        val notificationStore = given<NotificationStore>()
        val permissionManager = given<PermissionManager>()

        ResourceBox(
            resource = remember {
                permissionManager.hasPermissions(notificationPermission)
            }.collectAsResource()
        ) { hasPermission ->
            val scope = compositionScope()

            if (hasPermission) {
                ResourceLazyColumnItems(
                    resource = notificationStore.notifications.collectAsResource(),
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
                                    sbn.notification.extras.getCharSequence(Notification.EXTRA_TITLE)
                                        ?.toString() ?: ""
                                )
                            },
                            subtitle = {
                                Text(
                                    sbn.notification.extras.getCharSequence(Notification.EXTRA_TEXT)
                                        ?.toString() ?: ""
                                )
                            },
                            onClick = {
                                scope.launch {
                                    notificationStore.openNotification(sbn.notification)
                                }
                            },
                            leading = {
                                val context = ContextAmbient.current
                                ResourceBox(
                                    modifier = Modifier.size(40.dp)
                                        .drawBackground(
                                            color = Color(sbn.notification.color),
                                            shape = CircleShape
                                        )
                                        .padding(all = 8.dp),
                                    resource = produceResource {
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
                                    error = {
                                        Icon(Icons.Default.Error)
                                    }
                                )
                            },
                            trailing = if (sbn.isClearable) {
                                {
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                notificationStore.dismissNotification(sbn.key)
                                            }
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
                        onClick = {
                            scope.launch {
                                permissionManager.request(notificationPermission)
                            }
                        }
                    ) {
                        Text("Request")
                    }
                }
            }
        }
    }
}
