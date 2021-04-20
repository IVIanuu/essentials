package com.ivianuu.essentials.billing

import com.android.billingclient.api.*
import com.github.michaelbull.result.*
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.optics.*
import com.ivianuu.essentials.util.*
import com.ivianuu.injekt.*
import com.ivianuu.injekt.scope.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.*
import java.io.*
import kotlin.coroutines.*

interface BillingContext {
    val billingClient: BillingClient
    val logger: Logger
    val refreshes: MutableSharedFlow<BillingRefresh>
    suspend fun <R> withConnection(block: suspend BillingContext.() -> R): R
}

@Given
@Scoped<AppGivenScope>
class BillingContextImpl(
    @Given override val billingClient: BillingClient,
    @Given private val dispatcher: IODispatcher,
    @Given override val logger: Logger,
    @Given override val refreshes: MutableSharedFlow<BillingRefresh>,
    @Given private val scope: ScopeCoroutineScope<AppGivenScope>
) : BillingContext {
    private var isConnected = false
    private val connectionMutex = Mutex()
    override suspend fun <R> withConnection(block: suspend BillingContext.() -> R): R =
        withContext(scope.coroutineContext + dispatcher) {
            ensureConnected()
            block()
        }
    private suspend fun ensureConnected() = connectionMutex.withLock {
        if (isConnected) return@withLock
        suspendCoroutine { continuation ->
            logger.d { "start connection" }
            billingClient.startConnection(
                object : BillingClientStateListener {
                    override fun onBillingSetupFinished(result: BillingResult) {
                        if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                            logger.d { "connected" }
                            isConnected = true
                            continuation.resume(Unit)
                        } else {
                            logger.d { "connecting failed ${result.responseCode} ${result.debugMessage}" }
                            continuation.resumeWithException(IllegalStateException("Could not connect ${result.responseCode}"))
                        }
                    }

                    override fun onBillingServiceDisconnected() {
                        logger.d { "on billing service disconnected" }
                    }
                }
            )
        }
    }
}
