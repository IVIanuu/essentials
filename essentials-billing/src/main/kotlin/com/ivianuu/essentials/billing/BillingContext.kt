/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
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
import com.ivianuu.injekt.coroutines.IOContext
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

  suspend fun <R> withConnection(block: suspend context(BillingContext) () -> R): R?
}

@Provide @Scoped<AppScope> class BillingContextImpl(
  override val billingClient: BillingClient,
  private val context: IOContext,
  @property:Provide override val logger: Logger,
  override val refreshes: MutableSharedFlow<BillingRefresh>,
  private val scope: NamedCoroutineScope<AppScope>
) : BillingContext {
  private var isConnected = false
  private val connectionLock = Mutex()

  override suspend fun <R> withConnection(block: suspend context(BillingContext) () -> R): R? =
    withContext(scope.coroutineContext + context) {
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
