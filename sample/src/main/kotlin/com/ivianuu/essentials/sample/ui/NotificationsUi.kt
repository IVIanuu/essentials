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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.runCatching
import com.ivianuu.essentials.coroutines.parMap
import com.ivianuu.essentials.notificationlistener.EsNotificationListenerService
import com.ivianuu.essentials.notificationlistener.NotificationsAction
import com.ivianuu.essentials.permission.PermissionBinding
import com.ivianuu.essentials.permission.PermissionRequester
import com.ivianuu.essentials.permission.PermissionState
import com.ivianuu.essentials.permission.notificationlistener.NotificationListenerPermission
import com.ivianuu.essentials.resource.Idle
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.flowAsResource
import com.ivianuu.essentials.sample.R
import com.ivianuu.essentials.store.Collector
import com.ivianuu.essentials.ui.animatedstack.AnimatedBox
import com.ivianuu.essentials.ui.image.toImageBitmap
import com.ivianuu.essentials.ui.layout.center
import com.ivianuu.essentials.ui.material.ListItem
import com.ivianuu.essentials.ui.material.Scaffold
import com.ivianuu.essentials.ui.material.TopAppBar
import com.ivianuu.essentials.ui.navigation.Key
import com.ivianuu.essentials.ui.navigation.KeyModule
import com.ivianuu.essentials.ui.navigation.KeyUi
import com.ivianuu.essentials.ui.navigation.KeyUiGivenScope
import com.ivianuu.essentials.ui.resource.ResourceLazyColumnFor
import com.ivianuu.essentials.coroutines.updateIn
import com.ivianuu.essentials.notificationlistener.NotificationsState
import com.ivianuu.essentials.store.ScopeStateStore
import com.ivianuu.essentials.store.State
import com.ivianuu.injekt.Given
import com.ivianuu.injekt.android.AppContext
import com.ivianuu.injekt.common.typeKeyOf
import com.ivianuu.injekt.scope.Scoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlin.reflect.KClass

@Given
val notificationsHomeItem = HomeItem("Notifications") { NotificationsKey() }

class NotificationsKey : Key<Nothing>

@Given
val notificationsKeyModule = KeyModule<NotificationsKey>()

@Given
fun notificationsUi(@Given viewModel: NotificationsUiViewModel): KeyUi<NotificationsKey> = {
    val state by viewModel.collectAsState()
    Scaffold(topBar = { TopAppBar(title = { Text("Notifications") }) }) {
        AnimatedBox(state.hasPermissions) { hasPermission ->
            if (hasPermission) {
                NotificationsList(
                    notifications = state.notifications,
                    onNotificationClick = { viewModel.openNotification(it) },
                    onDismissNotificationClick = { viewModel.dismissNotification(it) }
                )
            } else {
                NotificationPermissions { viewModel.requestPermissions() }
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

@Scoped<KeyUiGivenScope>
@Given
class NotificationsUiViewModel(
    @Given private val appContext: AppContext,
    @Given private val dispatchServiceAction: Collector<NotificationsAction>,
    @Given private val permissionState: Flow<PermissionState<SampleNotificationsPermission>>,
    @Given private val permissionRequester: PermissionRequester,
    @Given private val serviceState: Flow<NotificationsState>,
    @Given private val store: ScopeStateStore<KeyUiGivenScope, NotificationsUiState>
) : StateFlow<NotificationsUiState> by store {
    init {
        serviceState
            .map { it.notifications }
            .map { notifications ->
                notifications
                    .parMap { it.toUiNotification() }
            }
            .flowAsResource()
            .updateIn(store) { copy(notifications = it) }
        permissionState
            .updateIn(store) { copy(hasPermissions = it) }
    }
    fun requestPermissions() = store.effect {
        permissionRequester(listOf(typeKeyOf<SampleNotificationsPermission>()))
    }
    fun openNotification(notification: UiNotification) = store.effect {
        dispatchServiceAction(
            NotificationsAction.OpenNotification(notification.sbn.notification)
        )
    }
    fun dismissNotification(notification: UiNotification) = store.effect {
        dispatchServiceAction(
            NotificationsAction.DismissNotification(notification.sbn.key)
        )
    }

    private fun StatusBarNotification.toUiNotification() = UiNotification(
        title = notification.extras.getCharSequence(Notification.EXTRA_TITLE)
            ?.toString() ?: "",
        text = notification.extras.getCharSequence(Notification.EXTRA_TEXT)
            ?.toString() ?: "",
        icon = runCatching {
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
                { Icon(painterResource(R.drawable.es_ic_error), null) }
            }
        ),
        color = Color(notification.color),
        isClearable = isClearable,
        sbn = this
    )
}

@PermissionBinding
@Given
object SampleNotificationsPermission : NotificationListenerPermission {
    override val serviceClass: KClass<out NotificationListenerService>
        get() = EsNotificationListenerService::class
    override val title: String = "Notifications"
    override val icon: @Composable (() -> Unit)?
        get() = null
}

data class NotificationsUiState(
    val hasPermissions: Boolean = false,
    val notifications: Resource<List<UiNotification>> = Idle,
) : State()

data class UiNotification(
    val title: String,
    val text: String,
    val icon: @Composable () -> Unit,
    val color: Color,
    val isClearable: Boolean,
    val sbn: StatusBarNotification
)
