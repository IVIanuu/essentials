/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.unlock

import android.annotation.*
import android.content.*
import android.os.*
import android.view.*
import androidx.activity.*
import androidx.lifecycle.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.android.*
import kotlinx.coroutines.*

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

    @Provide val component = application
      .let { it as AppElementsOwner }
      .appElements<ScreenOnActivityComponent>()

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

@Provide @Element<AppScope>
data class ScreenOnActivityComponent(
  val logger: Logger,
  val powerManager: @SystemService PowerManager
)
