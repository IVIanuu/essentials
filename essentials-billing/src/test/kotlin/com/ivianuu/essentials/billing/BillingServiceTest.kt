/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.billing

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.ivianuu.essentials.AppScope
import com.ivianuu.essentials.app.AppForegroundState
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.essentials.coroutines.ScopedCoroutineScope
import com.ivianuu.essentials.logging.NoopLogger
import com.ivianuu.essentials.test.dispatcher
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.essentials.util.AppUiStarter
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.inject
import io.kotest.matchers.booleans.shouldBeTrue
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [24])
class BillingServiceTest {
  @Provide val logger = NoopLogger
  @Provide val appForegroundState = flowOf(AppForegroundState.FOREGROUND)
  @Provide val appUiStarter = AppUiStarter { mockk() }

  @Test fun testWithConnection() = runCancellingBlockingTest {
    @Provide val scope: ScopedCoroutineScope<AppScope> = inject<CoroutineScope>()

    val context = BillingServiceImpl(
      billingClient = mockk {
        every { startConnection(any()) } answers {
          arg<BillingClientStateListener>(0)
            .onBillingSetupFinished(
              BillingResult.newBuilder()
                .setResponseCode(BillingClient.BillingResponseCode.OK)
                .build()
            )
        }
      },
      ioCoroutineContext = dispatcher,
      refreshes = MutableSharedFlow()
    )
    var ran = false
    context.withConnection { ran = true }
    ran.shouldBeTrue()
  }

  @Test fun testWithConnectionWithMultipleCallsToFinish() = runCancellingBlockingTest {
    @Provide val scope: ScopedCoroutineScope<AppScope> = inject<CoroutineScope>()

    val context = BillingServiceImpl(
      billingClient = mockk {
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
      },
      ioCoroutineContext = dispatcher,
      refreshes = MutableSharedFlow()
    )
    var ran = false
    context.withConnection { ran = true }
    ran.shouldBeTrue()
  }

  @Test fun testPurchaseUseCase() = runCancellingBlockingTest {
    @Provide val scope: ScopedCoroutineScope<AppScope> = inject<CoroutineScope>()
    @Provide val refreshes = EventFlow<BillingRefresh>()

    val service = BillingServiceImpl(
      billingClient = TestBillingClient { refreshes.tryEmit(BillingRefresh) }.withTestSku(),
      ioCoroutineContext = dispatcher
    )
    service.purchase(TestSku).shouldBeTrue()
  }
}
