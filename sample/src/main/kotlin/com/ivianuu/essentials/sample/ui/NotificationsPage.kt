/*
 * Copyright 2020 Manuel Wrage
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
import android.service.notification.StatusBarNotification
import androidx.compose.foundation.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
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
import androidx.compose.material.Button
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.coroutines.parallelMap
import com.ivianuu.essentials.notificationlistener.DefaultNotificationListenerService
import com.ivianuu.essentials.notificationlistener.NotificationStore
import com.ivianuu.essentials.permission.Permission
import com.ivianuu.essentials.permission.Title
import com.ivianuu.essentials.permission.hasPermissions
import com.ivianuu.essentials.permission.notificationlistener.NotificationListenerPermission
import com.ivianuu.essentials.permission.requestPermissions
import com.ivianuu.essentials.permission.to
import com.ivianuu.essentials.result.fold
import com.ivianuu.essentials.result.runCatching
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.NotificationsAction.DismissNotification
import com.ivianuu.essentials.sample.ui.NotificationsAction.OpenNotification
import com.ivianuu.essentials.sample.ui.NotificationsAction.RequestPermissions
import com.ivianuu.essentials.store.setStateIn
import com.ivianuu.essentials.store.store
import com.ivianuu.essentials.tuples.combine
import com.ivianuu.essentials.ui.animatedstack.AnimatedBox
import com.ivianuu.essentials.ui.image.toImageAsset
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.resource.Idle
import com.ivianuu.essentials.ui.resource.Resource
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnFor
import com.ivianuu.essentials.ui.resource.flowAsResource
import com.ivianuu.essentials.ui.store.component1
import com.ivianuu.essentials.ui.store.component2
import com.ivianuu.essentials.ui.store.rememberStore
import com.ivianuu.essentials.util.exhaustive
import com.ivianuu.injekt.Binding
import com.ivianuu.injekt.FunBinding
import com.ivianuu.injekt.android.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
                        dispatch(OpenNotification(it))
                    },
                    onDismissNotificationClick = {
                        dispatch(DismissNotification(it))
                    }
                )
            } else {
                NotificationPermissions {
                    dispatch(RequestPermissions)
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
                            .padding(all = 8.dp)
                    ) {
                        notification.icon()
                    }
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
    notifications: UiNotifications,
    notificationStore: NotificationStore,
    requestPermissions: requestPermissions,
) = store<NotificationsState, NotificationsAction>(NotificationsState()) {
    val permission = NotificationListenerPermission(
        DefaultNotificationListenerService::class,
        Permission.Title to "Notifications"
    )

    combine(
        hasPermissions(listOf(permission)),
        notifications.flowAsResource()
    ).setStateIn(this) { (hasPermissions: Boolean, notifications) ->
        copy(hasPermissions = hasPermissions, notifications = notifications)
    }

    for (action in this) {
        when (action) {
            is RequestPermissions -> {
                requestPermissions(listOf(permission))
            }
            is OpenNotification -> {
                notificationStore.openNotification(action.notification.sbn.notification)
            }
            is DismissNotification -> {
                notificationStore.dismissNotification(action.notification.sbn.key)
            }
        }.exhaustive
    }
}

typealias UiNotifications = Flow<List<UiNotification>>

@Binding
fun notifications(
    applicationContext: ApplicationContext,
    notificationStore: NotificationStore,
): UiNotifications {
    return notificationStore.notifications
        .map { notifications ->
            notifications
                .parallelMap { sbn ->
                    UiNotification(
                        title = sbn.notification.extras.getCharSequence(Notification.EXTRA_TITLE)
                            ?.toString() ?: "",
                        text = sbn.notification.extras.getCharSequence(Notification.EXTRA_TEXT)
                            ?.toString() ?: "",
                        icon = runCatching {
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
    object RequestPermissions : NotificationsAction()
    data class OpenNotification(val notification: UiNotification) : NotificationsAction()
    data class DismissNotification(val notification: UiNotification) : NotificationsAction()
}

data class UiNotification(
    val title: String,
    val text: String,
    val icon: @Composable () -> Unit,
    val color: Color,
    val isClearable: Boolean,
    val sbn: StatusBarNotification
)
