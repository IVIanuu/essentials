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
import androidx.compose.material.Button
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ivianuu.essentials.notificationlistener.DefaultNotificationListenerService
import com.ivianuu.essentials.notificationlistener.NotificationsAction
import com.ivianuu.essentials.notificationlistener.NotificationsState
import com.ivianuu.essentials.permission.PermissionBinding
import com.ivianuu.essentials.permission.PermissionRequester
import com.ivianuu.essentials.permission.PermissionState
import com.ivianuu.essentials.permission.notificationlistener.NotificationListenerPermission
import com.ivianuu.essentials.result.fold
import com.ivianuu.essentials.result.runKatching
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.NotificationsUiAction.DismissNotification
import com.ivianuu.essentials.sample.ui.NotificationsUiAction.OpenNotification
import com.ivianuu.essentials.sample.ui.NotificationsUiAction.RequestPermissions
import com.ivianuu.essentials.store.Actions
import com.ivianuu.essentials.store.DispatchAction
import com.ivianuu.essentials.store.Initial
import com.ivianuu.essentials.store.state
import com.ivianuu.essentials.tuples.parMap
import com.ivianuu.essentials.ui.animatedstack.AnimatedBox
import com.ivianuu.essentials.ui.core.Icon
import com.ivianuu.essentials.ui.image.toImageBitmap
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.resource.Idle
import com.ivianuu.essentials.ui.resource.Resource
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnFor
import com.ivianuu.essentials.ui.resource.flowAsResource
import com.ivianuu.essentials.ui.store.UiState
import com.ivianuu.essentials.ui.store.UiStateBinding
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.Module
import com.ivianuu.injekt.android.AppContext
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlin.reflect.KClass

@HomeItemBinding
@Given
val notificationsHomeItem = HomeItem("Notifications") { NotificationsKey() }

class NotificationsKey : Key<Nothing>

@Module
val notificationsKeyModule = KeyModule<NotificationsKey>()

@Given
fun notificationsUi(
    @Given stateProvider: @Composable () -> @UiState NotificationsUiState,
    @Given dispatch: DispatchAction<NotificationsUiAction>,
): KeyUi<NotificationsKey> = {
    val state = stateProvider()
    Scaffold(
        topBar = { TopAppBar(title = { Text("Notifications") }) }
    ) {
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
                            Icon(R.drawable.es_ic_clear, null)
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

@UiStateBinding
@Given
fun notificationState(
    @Given scope: CoroutineScope,
    @Given initial: @Initial NotificationsUiState = NotificationsUiState(),
    @Given actions: Actions<NotificationsUiAction>,
    @Given dispatchServiceAction: DispatchAction<NotificationsAction>,
    @Given notifications: Flow<UiNotifications>,
    @Given permissionState: PermissionState<SampleNotificationsPermission>,
    @Given permissionRequester: PermissionRequester
): StateFlow<NotificationsUiState> = scope.state(initial) {
    permissionState
        .reduce { copy(hasPermissions = it) }
        .launchIn(this)
    notifications.flowAsResource()
        .reduce { copy(notifications = it) }
        .launchIn(this)
    actions
        .onEach { action ->
            when (action) {
                is RequestPermissions -> permissionRequester(listOf(typeKeyOf<SampleNotificationsPermission>()))
                is OpenNotification -> dispatchServiceAction(
                    NotificationsAction.OpenNotification(action.notification.sbn.notification)
                )
                is DismissNotification -> dispatchServiceAction(
                    NotificationsAction.DismissNotification(action.notification.sbn.key)
                )
            }
        }
        .launchIn(this)
}

@PermissionBinding
@Given
object SampleNotificationsPermission : NotificationListenerPermission {
    override val serviceClass: KClass<out NotificationListenerService>
        get() = DefaultNotificationListenerService::class
    override val title: String = "Notifications"
    override val icon: @Composable (() -> Unit)?
        get() = null
}

typealias UiNotifications = List<UiNotification>

@Given
fun notifications(
    @Given appContext: AppContext,
    @Given serviceState: Flow<NotificationsState>,
): Flow<UiNotifications> = serviceState
    .map { it.notifications }
    .map { notifications ->
        notifications
            .parMap { it.toUiNotification(appContext) }
    }

private fun StatusBarNotification.toUiNotification(appContext: AppContext) = UiNotification(
    title = notification.extras.getCharSequence(Notification.EXTRA_TITLE)
        ?.toString() ?: "",
    text = notification.extras.getCharSequence(Notification.EXTRA_TEXT)
        ?.toString() ?: "",
    icon = runKatching {
        notification.smallIcon
            .loadDrawable(appContext)
            .toImageBitmap()

    }.fold(
        success = { bitmap ->
            {
                Image(
                    modifier = Modifier.size(24.dp),
                    bitmap = bitmap,
                    contentDescription = null
                )
            }
        },
        failure = {
            { Icon(R.drawable.es_ic_error, null) }
        }
    ),
    color = Color(notification.color),
    isClearable = isClearable,
    sbn = this
)

data class NotificationsUiState(
    val hasPermissions: Boolean = false,
    val notifications: Resource<List<UiNotification>> = Idle,
)

sealed class NotificationsUiAction {
    object RequestPermissions : NotificationsUiAction()
    data class OpenNotification(val notification: UiNotification) : NotificationsUiAction()
    data class DismissNotification(val notification: UiNotification) : NotificationsUiAction()
}

data class UiNotification(
    val title: String,
    val text: String,
    val icon: @Composable () -> Unit,
    val color: Color,
    val isClearable: Boolean,
    val sbn: StatusBarNotification
)
