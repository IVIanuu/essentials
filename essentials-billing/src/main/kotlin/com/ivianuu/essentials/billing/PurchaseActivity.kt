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

package com.ivianuu.essentials.billing

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.ivianuu.essentials.android.ui.base.EsActivity
import com.ivianuu.injekt.getLazy
import kotlinx.coroutines.launch

class PurchaseActivity : EsActivity() {

    private val manager: PurchaseManager by getLazy()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val requestId = intent.getStringExtra(KEY_REQUEST_ID)
        if (requestId == null) {
            finish()
            return
        }

        lifecycleScope.launch {
            manager.purchaseInternal(requestId, this@PurchaseActivity)
            finish() // finish once purchaseInternal returns
        }
    }

    override fun content() {
    }

    internal companion object {
        private const val KEY_REQUEST_ID = "request_id"

        fun purchase(
            context: Context,
            requestId: String
        ) {
            context.startActivity(
                Intent(
                    context,
                    PurchaseActivity::class.java
                ).apply {
                    putExtra(KEY_REQUEST_ID, requestId)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        }
    }
}
