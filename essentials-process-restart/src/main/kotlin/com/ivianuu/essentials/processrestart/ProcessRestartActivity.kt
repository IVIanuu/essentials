/*
 * Copyright 2021 Manuel Wrage
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

package com.ivianuu.essentials.processrestart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

class ProcessRestartActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val restartIntent = intent.getParcelableExtra<Intent>(KEY_RESTART_INTENT)
    if (restartIntent != null) {
      startActivity(restartIntent)
    }

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
