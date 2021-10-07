package com.ivianuu.essentials.unlock

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PowerManager
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.ivianuu.essentials.coroutines.onCancel
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.ActivityScope
import com.ivianuu.injekt.android.SystemService
import com.ivianuu.injekt.android.activityScope
import com.ivianuu.injekt.scope.ScopeElement
import com.ivianuu.injekt.scope.requireElement
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

/**
 * Turns the screen on
 */
class ScreenOnActivity : ComponentActivity() {
  @SuppressLint("NewApi")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val requestId = intent.getStringExtra(KEY_REQUEST_ID) ?: run {
      finish()
      return
    }

    @Provide val component: ScreenOnActivityComponent = requireElement(activityScope)

    log(logger = component.logger) { "turn screen on for $requestId" }

    window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
    window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)

    var hasResult = false

    lifecycleScope.launch {
      while (!component.powerManager.isInteractive) {
        yield()
      }

      hasResult = true
      onScreenOnResult(requestId, true)
      finish()
    }

    lifecycleScope.launch(start = CoroutineStart.UNDISPATCHED) {
      onCancel {
        // just in case we didn't respond yet
        if (!hasResult)
          onScreenOnResult(requestId, false)
      }
    }
  }

  internal companion object {
    private const val KEY_REQUEST_ID = "request_id"

    fun turnOn(context: Context, requestId: String) {
      context.startActivity(
        Intent(context, ScreenOnActivity::class.java).apply {
          putExtra(KEY_REQUEST_ID, requestId)
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
      )
    }
  }
}

@Provide @ScopeElement<ActivityScope> class ScreenOnActivityComponent(
  val logger: Logger,
  val powerManager: @SystemService PowerManager
)
