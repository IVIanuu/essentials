package com.ivianuu.essentials.util

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ivianuu.essentials.AndroidComponent
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.essentials.ui.navigation.AppUiStarter
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

interface RemoteAction<I : Any?> {
  suspend operator fun invoke()

  companion object {
    @Provide fun <@Spread T : RemoteAction<I>, I : Any?> binding(
      clazz: KClass<T>,
      factory: (I) -> T
    ): Pair<KClass<RemoteAction<*>>, (Any?) -> RemoteAction<*>> = (clazz to factory).cast()
  }
}

fun <T : RemoteAction<Any?>> remoteActionOf(
  context: Context,
  @Inject actionClass: KClass<T>,
  @Inject json: Json
): PendingIntent = remoteActionOf<T, Any?>(context = context, input = null)

fun <T : RemoteAction<I>, I : Any?> remoteActionOf(
  context: Context,
  input: I? = null,
  @Inject actionClass: KClass<T>,
  @Inject json: Json
): PendingIntent = PendingIntent.getBroadcast(
  context,
  0,
  Intent("execute_remote_action").apply {
    `package` = context.packageName
    putExtra("action_class", actionClass.java.name)
    if (input != null) {
      putExtra("input_class", input!!::class.java)
      putExtra("action_input", json.encodeToString(json.serializersModule.serializer(input!!::class.java), input))
    }
  },
  PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
)

fun interface StartAppRemoteAction : RemoteAction<Any?> {
  companion object {
    @Provide fun impl(appUiStarter: AppUiStarter) = StartAppRemoteAction {
      appUiStarter()
    }
  }
}

@Provide @AndroidComponent class RemoteActionReceiver(
  private val actionFactories: Map<KClass<RemoteAction<*>>, (Any?) -> RemoteAction<*>>,
  private val json: Json,
  private val logger: Logger,
  private val scope: ScopedCoroutineScope<AppScope>
) : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    scope.launch {
      logger.log { "on intent ${intent.extras}" }

      val input = intent.getStringExtra("input_class")
        ?.let { Class.forName(it) }
        ?.let {
          json.decodeFromString(
            json.serializersModule.serializer(it),
            intent.getStringExtra("action_input") ?: return@let null
          )
        }

      val action = intent.getStringExtra("action_class")
        ?.let { Class.forName(it).kotlin }
        ?.let { actionFactories[it]?.invoke(input) }
        ?: return@launch

      logger.log { "execute remote action ${action::class.java.name} with $input" }

      action.invoke()
    }
  }
}
