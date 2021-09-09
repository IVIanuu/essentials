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
