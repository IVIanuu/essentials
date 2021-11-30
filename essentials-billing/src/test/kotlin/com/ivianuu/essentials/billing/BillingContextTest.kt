/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.billing

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.ivianuu.essentials.logging.NoopLogger
import com.ivianuu.essentials.test.dispatcher
import com.ivianuu.essentials.test.runCancellingBlockingTest
import io.kotest.matchers.booleans.shouldBeTrue
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [24])
class BillingContextTest {
  @Test fun testWithConnection() = runCancellingBlockingTest {
    val context = BillingContextImpl(
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
      dispatcher = dispatcher,
      logger = NoopLogger,
      refreshes = MutableSharedFlow(),
      scope = this
    )
    var ran = false
    context.withConnection { ran = true }
    ran.shouldBeTrue()
  }

  @Test fun testWithConnectionWithMultipleCallsToFinish() = runCancellingBlockingTest {
    val context = BillingContextImpl(
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
      dispatcher = dispatcher,
      logger = NoopLogger,
      refreshes = MutableSharedFlow(),
      scope = this
    )
    var ran = false
    context.withConnection { ran = true }
    ran.shouldBeTrue()
  }
}
