/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.billing

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ivianuu.essentials.test.runCancellingBlockingTest
import com.ivianuu.injekt.Provide
import io.kotest.matchers.booleans.shouldBeTrue
import io.mockk.mockk
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [24])
class BillingUseCasesTest {
  @Test fun testPurchaseUseCase() = runCancellingBlockingTest {
    @Provide val context = TestBillingContext(this).apply {
      billingClient.withTestSku()
    }
    val useCase = purchaseUseCase(appUiStarter = { mockk() })
    useCase(TestSku, true, true)
      .shouldBeTrue()
  }
}
