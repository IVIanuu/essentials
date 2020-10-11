package com.ivianuu.essentials.sample.ui

import android.app.Notification
import android.service.notification.StatusBarNotification
import androidx.compose.foundation.Box
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.github.michaelbull.result.fold
import com.ivianuu.essentials.coroutines.parallelMap
import com.ivianuu.essentials.notificationlistener.DefaultNotificationListenerService
import com.ivianuu.essentials.notificationlistener.NotificationStore
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.Title
import com.ivianuu.essentials.permission.hasPermissions
import com.ivianuu.essentials.permission.notificationlistener.NotificationListenerPermission
import com.ivianuu.essentials.permission.requestPermissions
import com.ivianuu.essentials.permission.withValue
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.NotificationsAction.DismissNotificationClicked
import com.ivianuu.essentials.sample.ui.NotificationsAction.NotificationClicked
import com.ivianuu.essentials.sample.ui.NotificationsAction.RequestPermissionsClicked
import com.ivianuu.essentials.store.onEachAction
import com.ivianuu.essentials.store.setState
import com.ivianuu.essentials.store.storeProvider
import com.ivianuu.essentials.ui.animatedstack.AnimatedBox
import com.ivianuu.essentials.ui.image.toImageAsset
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.resource.Idle
import com.ivianuu.essentials.ui.resource.Resource
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnFor
import com.ivianuu.essentials.ui.store.component1
import com.ivianuu.essentials.ui.store.component2
import com.ivianuu.essentials.ui.store.executeIn
import com.ivianuu.essentials.ui.store.rememberStore
import com.ivianuu.essentials.util.exhaustive
import com.ivianuu.essentials.util.runCatchingAndLog
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.android.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

@FunBinding
@Composable
fun NotificationsPage(store: rememberStore<NotificationsState, NotificationsAction>) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Notifications") }) }
    ) {
        val (state, dispatch) = store()

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
                            .padding(all = 8.dp),
                        children = notification.icon
                    )
                },
                trailing = if (notification.isClearable) {
                    {
                        IconButton(onClick = { onDismissNotificationClick(notification) }) {
                            Icon(vectorResource(R.drawable.es_ic_clear))
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

@Binding
fun notificationStore(
    hasPermissions: hasPermissions,
    notifications: notifications,
    notificationStore: NotificationStore,
    requestPermissions: requestPermissions,
) = storeProvider<NotificationsState, NotificationsAction>(NotificationsState()) {
    val permission = NotificationListenerPermission(
        DefaultNotificationListenerService::class,
        Permission.Title withValue "Notifications"
    )

    hasPermissions(listOf(permission))
        .onEach { setState { copy(hasPermissions = it) } }
        .launchIn(this)

    notifications()
        .executeIn(this) { copy(notifications = it) }

    onEachAction { action ->
        when (action) {
            is RequestPermissionsClicked -> {
                requestPermissions(listOf(permission))
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

@FunBinding
fun notifications(
    applicationContext: ApplicationContext,
    notificationStore: NotificationStore,
): Flow<List<UiNotification>> {
    return notificationStore.notifications
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
                                { Icon(vectorResource(R.drawable.es_ic_error)) }
                            }
                        ),
                        color = Color(sbn.notification.color),
                        isClearable = sbn.isClearable,
                        sbn = sbn
                    )
                }
        }
}

data class NotificationsState(
    val hasPermissions: Boolean = false,
    val notifications: Resource<List<UiNotification>> = Idle
)

sealed class NotificationsAction {
    object RequestPermissionsClicked : NotificationsAction()
    data class NotificationClicked(val notification: UiNotification) : NotificationsAction()
    data class DismissNotificationClicked(val notification: UiNotification) : NotificationsAction()
}

data class UiNotification(
    val title: String,
    val text: String,
    val icon: @Composable () -> Unit,
    val color: Color,
    val isClearable: Boolean,
    val sbn: StatusBarNotification
)
