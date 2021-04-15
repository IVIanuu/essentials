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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.runCatching
import com.ivianuu.essentials.coroutines.parMap
import com.ivianuu.essentials.notificationlistener.DismissNotificationUseCase
import com.ivianuu.essentials.notificationlistener.EsNotificationListenerService
import com.ivianuu.essentials.notificationlistener.Notifications
import com.ivianuu.essentials.notificationlistener.OpenNotificationUseCase
import com.ivianuu.essentials.permission.PermissionRequester
import com.ivianuu.essentials.permission.PermissionState
import com.ivianuu.essentials.permission.notificationlistener.NotificationListenerPermission
import com.ivianuu.essentials.resource.Idle
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.flowAsResource
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.sample.ui.NotificationsUiAction.DismissNotification
import com.ivianuu.essentials.sample.ui.NotificationsUiAction.OpenNotification
import com.ivianuu.essentials.sample.ui.NotificationsUiAction.RequestPermissions
import com.ivianuu.essentials.store.StoreBuilder
import com.ivianuu.essentials.store.action
import com.ivianuu.essentials.store.updateIn
import com.ivianuu.essentials.ui.animatedstack.AnimatedBox
import com.ivianuu.essentials.ui.image.toImageBitmap
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.navigation.StoreKeyUi
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnFor
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.AppContext
import com.ivianuu.injekt.common.typeKeyOf
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.reflect.KClass

@Given
val notificationsHomeItem = HomeItem("Notifications") { NotificationsKey() }

class NotificationsKey : Key<Nothing>

@Given
val notificationsUi: StoreKeyUi<NotificationsKey, NotificationsUiState, NotificationsUiAction> = {
    Scaffold(topBar = { TopAppBar(title = { Text("Notifications") }) }) {
        AnimatedBox(state.hasPermissions) { hasPermission ->
            if (hasPermission) {
                NotificationsList(
                    notifications = state.notifications,
                    onNotificationClick = { send(OpenNotification(it)) },
                    onDismissNotificationClick = { send(DismissNotification(it)) }
                )
            } else {
                NotificationPermissions { send(RequestPermissions) }
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
                            Icon(painterResource(R.drawable.es_ic_clear), null)
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

data class NotificationsUiState(
    val hasPermissions: Boolean = false,
    val notifications: Resource<List<UiNotification>> = Idle,
)

data class UiNotification(
    val title: String,
    val text: String,
    val icon: @Composable () -> Unit,
    val color: Color,
    val isClearable: Boolean,
    val sbn: StatusBarNotification
)

sealed class NotificationsUiAction {
    object RequestPermissions : NotificationsUiAction()
    data class OpenNotification(val notification: UiNotification) : NotificationsUiAction()
    data class DismissNotification(val notification: UiNotification) : NotificationsUiAction()
}

@Given
fun notificationsUiStore(
    @Given appContext: AppContext,
    @Given dismissNotification: DismissNotificationUseCase,
    @Given notifications: Flow<Notifications>,
    @Given openNotification: OpenNotificationUseCase,
    @Given permissionState: Flow<PermissionState<SampleNotificationsPermission>>,
    @Given permissionRequester: PermissionRequester
): StoreBuilder<KeyUiGivenScope, NotificationsUiState, NotificationsUiAction> = {
    notifications
        .map { notifications ->
            notifications
                .parMap { it.toUiNotification(appContext) }
        }
        .flowAsResource()
        .updateIn(this) { copy(notifications = it) }
    permissionState.updateIn(this) { copy(hasPermissions = it) }
    action<RequestPermissions> {
        permissionRequester(listOf(typeKeyOf<SampleNotificationsPermission>()))
    }
    action<OpenNotification> {
        openNotification(it.notification.sbn.notification)
    }
    action<DismissNotification> {
        dismissNotification(it.notification.sbn.key)
    }
}

private fun StatusBarNotification.toUiNotification(appContext: AppContext) = UiNotification(
    title = notification.extras.getCharSequence(Notification.EXTRA_TITLE)
        ?.toString() ?: "",
    text = notification.extras.getCharSequence(Notification.EXTRA_TEXT)
        ?.toString() ?: "",
    icon = runCatching {
        notification.smallIcon
            .loadDrawable(appContext)
            .toBitmap()
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
            { Icon(painterResource(R.drawable.es_ic_error), null) }
        }
    ),
    color = Color(notification.color),
    isClearable = isClearable,
    sbn = this
)

@Given
object SampleNotificationsPermission : NotificationListenerPermission {
    override val serviceClass: KClass<out NotificationListenerService>
        get() = EsNotificationListenerService::class
    override val title: String = "Notifications"
    override val icon: @Composable (() -> Unit)?
        get() = null
}
