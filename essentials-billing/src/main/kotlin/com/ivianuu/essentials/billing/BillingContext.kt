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

package com.ivianuu.essentials.billing

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.logging.Logger
import com.ivianuu.essentials.logging.log
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.common.Scoped
import com.ivianuu.injekt.coroutines.IODispatcher
import com.ivianuu.injekt.coroutines.NamedCoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface BillingContext {
  val billingClient: BillingClient

  @Provide val logger: Logger

  val refreshes: MutableSharedFlow<BillingRefresh>

  suspend fun <R> withConnection(block: suspend BillingContext.() -> R): R?
}

@Provide @Scoped<AppScope> class BillingContextImpl(
  override val billingClient: BillingClient,
  private val dispatcher: IODispatcher,
  override val logger: Logger,
  override val refreshes: MutableSharedFlow<BillingRefresh>,
  private val scope: NamedCoroutineScope<AppScope>
) : BillingContext {
  private var isConnected = false
  private val connectionLock = Mutex()

  override suspend fun <R> withConnection(block: suspend BillingContext.() -> R): R? =
    withContext(scope.coroutineContext + dispatcher) {
      ensureConnected()
      block()
    }

  private suspend fun ensureConnected(): Unit = connectionLock.withLock {
    if (isConnected) return@withLock
    suspendCoroutine<Unit?> { continuation ->
      log { "start connection" }
      billingClient.startConnection(
        object : BillingClientStateListener {
          private var completed = false
          override fun onBillingSetupFinished(result: BillingResult) {
            // for some reason on billing setup finished is sometimes called multiple times
            // we ensure that we we only resume once
            if (!completed) {
              completed = true
              if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                log { "connected" }
                isConnected = true
                continuation.resume(Unit)
              } else {
                log { "connecting failed ${result.responseCode} ${result.debugMessage}" }
                continuation.resume(null)
              }
            }
          }

          override fun onBillingServiceDisconnected() {
            log { "on billing service disconnected" }
          }
        }
      )
    }
  }
}
