/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.billing

import com.android.billingclient.api.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.logging.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.common.*
import com.ivianuu.injekt.coroutines.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.*
import kotlin.coroutines.*

interface BillingContext {
  val billingClient: BillingClient

  @Provide val logger: Logger

  val refreshes: MutableSharedFlow<BillingRefresh>

  suspend fun <R> withConnection(block: suspend BillingContext.() -> R): R?
}

@Provide @Scoped<AppScope> class BillingContextImpl(
  override val billingClient: BillingClient,
  private val context: IOContext,
  override val logger: Logger,
  override val refreshes: MutableSharedFlow<BillingRefresh>,
  private val scope: NamedCoroutineScope<AppScope>
) : BillingContext {
  private var isConnected = false
  private val connectionLock = Mutex()

  override suspend fun <R> withConnection(block: suspend BillingContext.() -> R): R? =
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
