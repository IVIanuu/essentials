/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.processrestart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.ComponentActivity

class ProcessRestartActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val restartIntent = intent.getParcelableExtra<Parcelable>(KEY_RESTART_INTENT)
    if (restartIntent != null)
      startActivity(restartIntent as Intent)

    finish()
    restartProcess()
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
