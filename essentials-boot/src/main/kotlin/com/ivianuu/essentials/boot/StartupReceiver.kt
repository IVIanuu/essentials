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

package com.ivianuu.essentials.boot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ivianuu.essentials.cast
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.d
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.android.ReceiverScope
import com.ivianuu.injekt.android.createReceiverScope
import com.ivianuu.injekt.scope.DisposableScope
import com.ivianuu.injekt.scope.ScopeElement
import com.ivianuu.injekt.scope.requireElement

class StartupReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
    val component = requireElement<StartupReceiverComponent>(createReceiverScope(context, intent))
    d(logger = component.logger) { "on system boot" }
    component.bootListeners.forEach { it() }
    component.receiverScope.cast<DisposableScope>().dispose()
  }
}

@Provide @ScopeElement<ReceiverScope>
class StartupReceiverComponent(
  val bootListeners: Set<BootListener> = emptySet(),
  val logger: Logger,
  val receiverScope: ReceiverScope,
)
