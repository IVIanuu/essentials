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
