/*
 * Copyright 2019 Manuel Wrage
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

package com.ivianuu.essentials.activityresult

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.ivianuu.essentials.android.ui.base.EsActivity
import com.ivianuu.injekt.getLazy

class ActivityResultActivity : EsActivity() {

    private val activityResultController: ActivityResultController by getLazy()

    private var isValid = true
    private var hasResult = false

    private var requestCode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isValid = intent.hasExtra(KEY_REQUEST_CODE) && intent.hasExtra(KEY_INTENT)

        if (!isValid) {
            finish()
            return
        }

        val activityResultIntent = intent.getParcelableExtra<Intent>(KEY_INTENT)!!
        requestCode = intent.getIntExtra(KEY_REQUEST_CODE, 0)
        val options = intent.getParcelableExtra<Bundle>(KEY_OPTIONS)

        startActivityForResult(activityResultIntent, requestCode, options)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        hasResult = true

        activityResultController.onActivityResult(ActivityResult(
            requestCode = requestCode,
            resultCode = resultCode,
            data = data
        ))

        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isValid && !hasResult) {
            activityResultController.onActivityResult(
                ActivityResult(
                    requestCode = requestCode,
                    resultCode = Activity.RESULT_CANCELED,
                    data = null
                )
            )
        }
    }

    override fun content() {
    }

    internal companion object {
        private const val KEY_REQUEST_CODE = "request_code"
        private const val KEY_INTENT = "intent"
        private const val KEY_OPTIONS = "options"
        fun startActivityForResult(
            context: Context,
            intent: Intent,
            requestCode: Int,
            options: Bundle?
        ) {
            context.startActivity(
                Intent(context, ActivityResultActivity::class.java).apply {
                    putExtra(KEY_INTENT, intent)
                    putExtra(KEY_REQUEST_CODE, requestCode)
                    putExtra(KEY_OPTIONS, options)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        }
    }
}
