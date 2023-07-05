package com.ivianuu.essentials.util

import android.app.NotificationChannel
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import androidx.compose.ui.graphics.toArgb
import com.ivianuu.essentials.AndroidComponent
import com.ivianuu.essentials.AppConfig
import com.ivianuu.essentials.AppContext
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.compose.compositionStateFlow
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.ui.AppColors
import com.ivianuu.essentials.ui.navigation.Model
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.common.SourceKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import android.app.Notification as AndroidNotification
import android.app.NotificationManager as AndroidNotificationManager

@Serializable abstract class Notification(
  val channelId: String,
  val channelName: String = channelId,
  val importance: Importance = Importance.DEFAULT
) {
  enum class Importance { MIN, LOW, DEFAULT, HIGH, MAX }
}

data class NotificationModel<N : Notification>(
  val icon: Icon,
  val image: Icon? = null,
  val title: String? = null,
  val text: String? = null,
  val onClick: (() -> Unit)? = null,
  val onGoing: Boolean = false,
  val autoDismiss: Boolean = true,
  val actions: List<Action> = emptyList(),
  val interceptor: (AndroidNotification.Builder.() -> Unit)? = null,
  val tag: String? = null,
  @Inject val id: NotificationId,
) {
  data class Action(
    val icon: Icon? = null,
    val title: String? = null,
    @Inject val id: ActionId,
    val onClick: (() -> Unit)? = null
  )

  companion object {
    @Provide fun <@Spread T : Model<NotificationModel<N>>, N : Notification> modelBinding(
      clazz: KClass<N>,
      model: (N) -> T
    ): Pair<KClass<Notification>, (Notification) -> Model<NotificationModel<*>>> = (clazz to model).cast()

    @Provide val defaultBindings get() =
      emptyList<Pair<KClass<Notification>, (Notification) -> Model<NotificationModel<*>>>>()
  }
}

interface NotificationManager {
  fun <N : Notification> notificationModels(scope: CoroutineScope, notification: N): StateFlow<NotificationModel<N>>

  fun toAndroidNotification(notification: Notification, model: NotificationModel<*>): AndroidNotification

  suspend fun postNotification(notification: Notification): Nothing
}

@JvmInline value class NotificationId(val value: Int) {
  companion object {
    @Provide fun default(sourceKey: SourceKey) = NotificationId(sourceKey.value.hashCode())
  }
}

val AndroidNotification.id: NotificationId
  get() = NotificationId(extras.getInt("id"))

val AndroidNotification.tag: String?
  get() = extras.getString("tag")

@JvmInline value class ActionId(val value: String) {
  companion object {
    @Provide fun default(sourceKey: SourceKey) = ActionId(sourceKey.value)
  }
}

@Provide class NotificationManagerImpl(
  private val androidNotificationManager: @SystemService AndroidNotificationManager,
  private val appColors: AppColors,
  private val appConfig: AppConfig,
  private val appContext: AppContext,
  private val json: Json,
  private val models: Map<KClass<Notification>, (Notification) -> Model<NotificationModel<*>>>,
) : NotificationManager {
  override fun <N : Notification> notificationModels(scope: CoroutineScope, notification: N): StateFlow<NotificationModel<N>> {
    val model = models[notification::class.cast()]?.invoke(notification) ?: error("No model found for $notification")
    return scope.compositionStateFlow { model().cast() }
  }

  override fun toAndroidNotification(notification: Notification, model: NotificationModel<*>): AndroidNotification {
    androidNotificationManager.createNotificationChannel(
      NotificationChannel(
        notification.channelId,
        notification.channelName,
        when (notification.importance) {
          Notification.Importance.MIN -> AndroidNotificationManager.IMPORTANCE_MIN
          Notification.Importance.LOW -> AndroidNotificationManager.IMPORTANCE_LOW
          Notification.Importance.DEFAULT -> AndroidNotificationManager.IMPORTANCE_DEFAULT
          Notification.Importance.HIGH -> AndroidNotificationManager.IMPORTANCE_HIGH
          Notification.Importance.MAX -> AndroidNotificationManager.IMPORTANCE_HIGH
        }
      )
    )

    return AndroidNotification.Builder(appContext, notification.channelId)
      .apply {
        setSmallIcon(model.icon)
        model.image?.let { setLargeIcon(it) }
        model.title?.let { setContentTitle(it) }
        model.text?.let { setContentText(it) }

        setColor(appColors.primary.toArgb())

        setOngoing(model.onGoing)
        setAutoCancel(model.autoDismiss)

        model.onClick?.let {
          setContentIntent(
            PendingIntent.getBroadcast(
              appContext,
              model.id.value.hashCode() + "full_screen".hashCode(),
              Intent("notification_action").apply {
                `package` = appConfig.packageName
                putExtra("notification_class", notification::class.java.name)
                putExtra("notification", json.encodeToString(json.serializersModule.serializer(notification::class.java), notification))
                putExtra("action_id", "full_screen")
              },
              PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
          )
        }

        model.actions.forEach { action ->
          addAction(
            AndroidNotification.Action.Builder(
              action.icon,
              action.title,
              PendingIntent.getBroadcast(
                appContext,
                model.id.value.hashCode() + action.id.value.hashCode(),
                Intent("notification_action").apply {
                  `package` = appConfig.packageName
                  putExtra("notification_class", notification::class.java.name)
                  putExtra("notification", json.encodeToString(json.serializersModule.serializer(notification::class.java), notification))
                  putExtra("action_id", action.id.value)
                },
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
              )
            )
              .build()
          )
        }

        extras.putInt("id", model.id.value)
        extras.putString("tag", model.tag)

        model.interceptor?.invoke(this)
      }
      .build()
  }

  override suspend fun postNotification(notification: Notification): Nothing = coroutineScope {
    notificationModels(this, notification)
      .map { toAndroidNotification(notification, it) }
      .collectLatest {
        androidNotificationManager.notify(
          it.extras.getString("tag"),
          it.extras.getInt("id"),
          it
        )

        onCancel { androidNotificationManager.cancel(it.extras.getInt("id")) }
      }

    awaitCancellation()
  }
}

@Provide @AndroidComponent class NotificationActionReceiver(
  private val json: Json,
  private val notificationManager: NotificationManager,
  private val scope: ScopedCoroutineScope<AppScope>
) : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    scope.launch {
      coroutineScope {
        val notificationClass = intent.getStringExtra("notification_class")
          ?.let { Class.forName(it) }
          ?: return@coroutineScope

        val notification = intent.getStringExtra("notification")
          ?.let { json.decodeFromString(json.serializersModule.serializer(notificationClass), it) as Notification }
          ?: return@coroutineScope

        val actionId = intent.getStringExtra("action_id")
          ?: return@coroutineScope

        val notificationModel = notificationManager.notificationModels(this, notification).first()

        when (actionId) {
          "full_screen" -> notificationModel.onClick?.invoke()
          else -> notificationModel.actions.firstOrNull { it.id.value == actionId }?.onClick?.invoke()
        }
      }
    }
  }
}
