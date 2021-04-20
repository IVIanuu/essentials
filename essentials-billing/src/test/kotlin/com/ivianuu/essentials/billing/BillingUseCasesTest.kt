package com.ivianuu.essentials.billing

import androidx.test.ext.junit.runners.*
import com.android.billingclient.api.*
import com.ivianuu.essentials.test.*
import io.kotest.matchers.booleans.*
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.*
import org.junit.runner.*
import org.robolectric.annotation.*

@RunWith(AndroidJUnit4::class)
@Config(sdk = [24])
class BillingUseCasesTest {
    @Test
    fun testPurchaseUseCase() = runCancellingBlockingTest {
        val context = TestBillingContext(this).apply {
            billingClient.withTestSku()
        }
        val useCase = purchaseUseCase(
            acknowledgePurchase = acknowledgePurchaseUseCase(context),
            appUiStarter = { mockk() },
            context = context,
            consumePurchase = consumePurchaseUseCase(context)
        )
        useCase(TestSku, true, true)
            .shouldBeTrue()
    }
}
