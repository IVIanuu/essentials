package essentials.util

import android.app.*
import android.content.*
import essentials.*
import essentials.coroutines.*
import essentials.logging.*
import essentials.ui.navigation.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlin.reflect.*

interface RemoteAction<I : Any?> {
  suspend fun execute()

  @Provide companion object {
    @Provide fun <@AddOn T : RemoteAction<I>, I : Any?> binding(
      clazz: KClass<T>,
      factory: (I) -> T
    ): Pair<KClass<RemoteAction<*>>, (Any?) -> RemoteAction<*>> = (clazz to factory).cast()
  }
}

@Provide class RemoteActionFactory(
  private val appContext: AppContext,
  private val json: Json
) {
  operator fun <T : RemoteAction<I>, I> invoke(input: I? = null, actionClass: KClass<T> = inject): PendingIntent =
    PendingIntent.getBroadcast(
      appContext,
      0,
      Intent("execute_remote_action").apply {
        `package` = appContext.packageName
        putExtra("action_class", actionClass.java.name)
        if (input != null) {
          putExtra("input_class", input!!::class.java)
          putExtra("action_input", json.encodeToString(json.serializersModule.serializer(input!!::class.java), input))
        }
      },
      PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
}

@Provide class StartAppRemoteAction(private val appUiStarter: AppUiStarter) : RemoteAction<Any?> {
  override suspend fun execute() {
    appUiStarter.startAppUi()
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
      logger.d { "on intent ${intent.extras}" }

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

      logger.d { "execute remote action ${action::class.java.name} with $input" }

      action.execute()
    }
  }
}
