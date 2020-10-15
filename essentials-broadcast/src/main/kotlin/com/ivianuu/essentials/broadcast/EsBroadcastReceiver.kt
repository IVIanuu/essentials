/*
 * Copyright 2020 Manuel Wrage
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
import com.ivianuu.injekt.android.ReceiverComponent
import com.ivianuu.injekt.android.createReceiverComponent

/**
 * Base broadcast receiver
 */
abstract class EsBroadcastReceiver : BroadcastReceiver() {

    lateinit var receiverComponent: ReceiverComponent

    override fun onReceive(context: android.content.Context, intent: Intent) {
        receiverComponent = createReceiverComponent(context, intent)
    }
}
