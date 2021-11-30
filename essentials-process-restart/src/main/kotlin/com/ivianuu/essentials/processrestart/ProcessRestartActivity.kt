/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.processrestart

import android.content.*
import android.os.*
import androidx.activity.*

class ProcessRestartActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val restartIntent = intent.getParcelableExtra<Intent>(KEY_RESTART_INTENT)
    if (restartIntent != null)
      startActivity(restartIntent)

    finish()
    Runtime.getRuntime().exit(0)
  }

  internal companion object {
    private const val KEY_RESTART_INTENT = "restart_intent"
    fun launch(context: Context, restartIntent: Intent) {
      context.startActivity(
        Intent(context, ProcessRestartActivity::class.java).apply {
          putExtra(KEY_RESTART_INTENT, restartIntent)
          addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
      )
    }
  }
}
