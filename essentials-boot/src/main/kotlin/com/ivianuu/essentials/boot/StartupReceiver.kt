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

import android.content.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.android.*
import com.ivianuu.injekt.scope.*

class StartupReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
    val component = createReceiverScope(context, intent)
      .element<StartupReceiverComponent>()
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
