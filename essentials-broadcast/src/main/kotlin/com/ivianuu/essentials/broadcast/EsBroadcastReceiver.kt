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

package com.ivianuu.essentials.broadcast

import android.content.BroadcastReceiver
import android.content.Intent
import com.ivianuu.injekt.Reader
import com.ivianuu.injekt.android.ReceiverContext
import com.ivianuu.injekt.android.createReceiverContext
import com.ivianuu.injekt.runReader

/**
 * Base broadcast receiver
 */
abstract class EsBroadcastReceiver : BroadcastReceiver() {

    lateinit var readerContext: ReceiverContext

    override fun onReceive(context: android.content.Context, intent: Intent) {
        readerContext = createReceiverContext(context, intent)
        readerContext.runReader { onReceive(intent) }
    }

    @Reader
    protected abstract fun onReceive(intent: Intent)

}
