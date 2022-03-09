/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.billing

import androidx.test.ext.junit.runners.*
import com.ivianuu.essentials.test.*
import io.kotest.matchers.booleans.*
import io.mockk.*
import org.junit.*
import org.junit.runner.*
import org.robolectric.annotation.*

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
