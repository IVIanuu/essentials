package com.ivianuu.essentials.sample.ui

import android.app.Notification
import android.service.notification.StatusBarNotification
import androidx.compose.Composable
import androidx.compose.Immutable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
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
import com.github.michaelbull.result.fold
import com.ivianuu.essentials.app.applicationContext
import com.ivianuu.essentials.coroutines.parallelMap
import com.ivianuu.essentials.notificationlistener.DefaultNotificationListenerService
import com.ivianuu.essentials.notificationlistener.NotificationStore
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.Title
import com.ivianuu.essentials.permission.hasPermissions
import com.ivianuu.essentials.permission.notificationlistener.NotificationListenerPermission
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.essentials.sample.ui.NotificationsAction.DismissNotificationClicked
import com.ivianuu.essentials.sample.ui.NotificationsAction.NotificationClicked
import com.ivianuu.essentials.sample.ui.NotificationsAction.RequestPermissionsClicked
import com.ivianuu.essentials.store.Store
import com.ivianuu.essentials.store.onEachAction
import com.ivianuu.essentials.store.setState
import com.ivianuu.essentials.store.store
import com.ivianuu.essentials.ui.animatedstack.AnimatedBox
import com.ivianuu.essentials.ui.image.toImageAsset
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.resource.Idle
import com.ivianuu.essentials.ui.resource.Resource
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnItems
import com.ivianuu.essentials.ui.store.component1
import com.ivianuu.essentials.ui.store.component2
import com.ivianuu.essentials.ui.store.executeIn
import com.ivianuu.essentials.ui.store.rememberStore
import com.ivianuu.essentials.util.exhaustive
import com.ivianuu.essentials.util.runCatchingAndLog
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.given
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@Reader
@Composable
fun NotificationsPage() {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Notifications") }) }
    ) {
        val (state, dispatch) = rememberStore { notificationStore() }

        AnimatedBox(state.hasPermissions) { hasPermission ->
            if (hasPermission) {
                NotificationsList(
                    notifications = state.notifications,
                    onNotificationClick = {
                        dispatch(NotificationClicked(it))
                    },
                    onDismissNotificationClick = {
                        dispatch(DismissNotificationClicked(it))
                    }
                )
            } else {
                NotificationPermissions {
                    dispatch(RequestPermissionsClicked)
                }
            }
        }
    }
}

@Composable
private fun NotificationsList(
    onNotificationClick: (UiNotification) -> Unit,
    onDismissNotificationClick: (UiNotification) -> Unit,
    notifications: Resource<List<UiNotification>>
) {
    ResourceLazyColumnItems(
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
                            .drawBackground(
                                color = notification.color,
                                shape = CircleShape
                            )
                            .padding(all = 8.dp),
                        children = notification.icon
                    )
                },
                trailing = if (notification.isClearable) {
                    {
                        IconButton(onClick = { onDismissNotificationClick(notification) }) {
                            Icon(Icons.Default.Clear)
                        }
                    }
                } else null
            )
        }
    )
}

@Composable
private fun NotificationPermissions(
    onRequestPermissionsClick: () -> Unit
) {
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
        Button(onClick = onRequestPermissionsClick) {
            Text("Request")
        }
    }
}

@Reader
private fun notificationStore(): Store<NotificationsState, NotificationsAction> {
    val permission = NotificationListenerPermission(
        DefaultNotificationListenerService::class,
        Permission.Title withValue "Notifications"
    )
    val notificationStore = given<NotificationStore>()
    return store(NotificationsState()) {
        hasPermissions(permission)
            .onEach { setState { copy(hasPermissions = it) } }
            .launchIn(scope)

        notifications()
            .executeIn(this) { copy(notifications = it) }

        onEachAction { action ->
            when (action) {
                is RequestPermissionsClicked -> {

                }
                is NotificationClicked -> {
                    notificationStore.openNotification(action.notification.sbn.notification)
                }
                is DismissNotificationClicked -> {
                    notificationStore.dismissNotification(action.notification.sbn.key)
                }
            }.exhaustive
        }
    }
}

@Reader
private fun notifications() = given<NotificationStore>().notifications
    .map { notifications ->
        notifications
            .parallelMap { sbn ->
                UiNotification(
                    title = sbn.notification.extras.getCharSequence(Notification.EXTRA_TITLE)
                        ?.toString() ?: "",
                    text = sbn.notification.extras.getCharSequence(Notification.EXTRA_TEXT)
                        ?.toString() ?: "",
                    icon = runCatchingAndLog {
                        sbn.notification.smallIcon
                            .loadDrawable(applicationContext)
                            .toImageAsset();

                    }.fold(
                        success = { asset ->
                            {
                                Image(
                                    modifier = Modifier.size(24.dp),
                                    asset = asset
                                )
                            }
                        },
                        failure = {
                            { Icon(Icons.Default.Error) }
                        }
                    ),
                    color = Color(sbn.notification.color),
                    isClearable = sbn.isClearable,
                    sbn = sbn
                )
            }
    }

@Immutable
private data class NotificationsState(
    val hasPermissions: Boolean = false,
    val notifications: Resource<List<UiNotification>> = Idle
)

private sealed class NotificationsAction {
    object RequestPermissionsClicked : NotificationsAction()
    data class NotificationClicked(val notification: UiNotification) : NotificationsAction()
    data class DismissNotificationClicked(val notification: UiNotification) : NotificationsAction()
}

@Immutable
private data class UiNotification(
    val title: String,
    val text: String,
    val icon: @Composable () -> Unit,
    val color: Color,
    val isClearable: Boolean,
    val sbn: StatusBarNotification
)
