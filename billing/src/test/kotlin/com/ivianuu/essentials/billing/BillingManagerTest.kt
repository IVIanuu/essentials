/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.billing

import androidx.test.ext.junit.runners.*
import com.android.billingclient.api.*
import com.ivianuu.essentials.*
import com.ivianuu.essentials.app.AppForegroundState
import com.ivianuu.essentials.coroutines.*
import com.ivianuu.essentials.test.*
import com.ivianuu.essentials.ui.navigation.*
import com.ivianuu.injekt.*
import io.kotest.matchers.booleans.*
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.*
import org.junit.runner.*
import org.robolectric.annotation.*

@RunWith(AndroidJUnit4::class)
@Config(sdk = [24])
class BillingManagerTest {
  @Provide val logger = NoopLogger
  @Provide val appForegroundState = flowOf(AppForegroundState.FOREGROUND)
  @Provide val appUiStarter = AppUiStarter { mockk() }

  @Test fun testWithConnection() = runCancellingBlockingTest {
    @Provide val scope: ScopedCoroutineScope<AppScope> = inject<CoroutineScope>()

    val context = BillingManager(
      billingClientFactory = {
        mockk {
          every { startConnection(any()) } answers {
            arg<BillingClientStateListener>(0)
              .onBillingSetupFinished(
                BillingResult.newBuilder()
                  .setResponseCode(BillingClient.BillingResponseCode.OK)
                  .build()
              )
          }
        }
      },
      coroutineContexts = CoroutineContexts(dispatcher),
      refreshes = MutableSharedFlow()
    )
    var ran = false
    context.withBillingClient { ran = true }
    ran.shouldBeTrue()
  }

  @Test fun testWithConnectionWithMultipleCallsToFinish() = runCancellingBlockingTest {
    @Provide val scope: ScopedCoroutineScope<AppScope> = inject<CoroutineScope>()

    val context = BillingManager(
      billingClientFactory = {
        mockk {
          every { startConnection(any()) } answers {
            val listener = arg<BillingClientStateListener>(0)
            listener.onBillingSetupFinished(
              BillingResult.newBuilder()
                .setResponseCode(BillingClient.BillingResponseCode.OK)
                .build()
            )
            listener.onBillingSetupFinished(
              BillingResult.newBuilder()
                .setResponseCode(BillingClient.BillingResponseCode.OK)
                .build()
            )
          }
        }
      },
      coroutineContexts = CoroutineContexts(dispatcher),
      refreshes = MutableSharedFlow()
    )
    var ran = false
    context.withBillingClient { ran = true }
    ran.shouldBeTrue()
  }

  @Test fun testPurchaseUseCase() = runCancellingBlockingTest {
    @Provide val scope: ScopedCoroutineScope<AppScope> = inject<CoroutineScope>()
    @Provide val refreshes = EventFlow<BillingRefresh>()

    val service = BillingManager(
      billingClientFactory = { TestBillingClient { refreshes.tryEmit(BillingRefresh) }.withTestSku() },
      coroutineContexts = CoroutineContexts(dispatcher)
    )
    service.purchase(TestSku).shouldBeTrue()
  }
}
